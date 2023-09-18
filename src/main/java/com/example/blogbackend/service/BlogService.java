package com.example.blogbackend.service;

import com.example.blogbackend.dto.projection.BlogPublic;
import com.example.blogbackend.entity.Blog;
import com.example.blogbackend.entity.Category;
import com.example.blogbackend.entity.User;
import com.example.blogbackend.exception.NotFoundException;
import com.example.blogbackend.repository.BlogRepository;
import com.example.blogbackend.repository.CategoryRepository;
import com.example.blogbackend.repository.UserRepository;
import com.example.blogbackend.request.UpsertBlogRequest;
import com.github.slugify.Slugify;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    //Danh sách tất cả bài viết phân trang
    public Page<BlogPublic> getBlogPage(Integer page, Integer pageSize) {
        Page<BlogPublic> blogPage = blogRepository.findByOrderByCreatedAtDesc(PageRequest.of(page, pageSize));
        return blogPage;
    }

    //Danh sách bài viết của tôi (id fix cứng)
    public List<BlogPublic> getAllOwnBlog(String email) {
        return blogRepository.findByUser_EmailOrderByCreatedAtDesc(email);
    }

    //Lấy chi tiết blog theo blog id
    public BlogPublic getBlogDetail(Integer blogId){
        Blog blog = blogRepository.findById(blogId).orElseThrow(() -> {
            throw new NotFoundException("Blog " + blogId + " not found");
        });
        return BlogPublic.of(blog);
    }

    //Thêm blog mới
    @Transactional
    public BlogPublic addNewBlog(UpsertBlogRequest request, String email){
        //find category list
        List<Category> categories = categoryRepository.findByIdIn(request.getCategoryIds());

        //userid fixed to 1
        User user = userRepository.findByEmail(email).orElseThrow(()-> {
            throw new NotFoundException("user not found");
        });
        Slugify slugify = Slugify.builder().build();
        Blog newBlog = Blog.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .content(request.getContent())
                .thumbnail(request.getThumbnail())
                .status(request.getStatus())
                .categories(categories)
                .slug(slugify.slugify(request.getTitle()))
                .comments(new ArrayList<>())
                .user(user)
                .build();

        blogRepository.save(newBlog);
        return BlogPublic.of(newBlog);
    }

    //Cập nhật blog
    @Transactional
    public BlogPublic updateBlog(Integer blogId, UpsertBlogRequest request) {
        //Blog exist?
        Blog blog2update = blogRepository.findById(blogId)
                .orElseThrow(() -> {throw new NotFoundException("not found blog id "+ blogId);});
        //find category list
        List<Category> categories = categoryRepository.findByIdIn(request.getCategoryIds());

        Slugify slugify = Slugify.builder().build();
        blog2update.setTitle(request.getTitle());
        blog2update.setDescription(request.getDescription());
        blog2update.setContent(request.getContent());
        blog2update.setThumbnail(request.getThumbnail());
        blog2update.setStatus(request.getStatus());
        blog2update.setCategories(categories);
        blog2update.setSlug(slugify.slugify(request.getTitle()));

        return BlogPublic.of(blog2update);
    }

    //Xóa blog
    @Transactional
    public void deleteBlog(Integer blogId){
        //Blog exist?
        Blog blog2delete = blogRepository.findById(blogId).orElseThrow(
                () -> { throw new NotFoundException("not found blog id "+ blogId);}
        );
        blogRepository.delete(blog2delete);
    }
}
