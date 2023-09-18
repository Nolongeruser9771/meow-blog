package com.example.blogbackend.service;

import com.example.blogbackend.dto.projection.BlogPublic;
import com.example.blogbackend.dto.projection.CategoryWebPublic;
import com.example.blogbackend.entity.Blog;
import com.example.blogbackend.entity.Category;
import com.example.blogbackend.exception.NotFoundException;
import com.example.blogbackend.repository.BlogRepository;
import com.example.blogbackend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    //1. Lấy danh sách blog
    public Page<BlogPublic> getAllBlog(Integer page, Integer pageSize) {
        //Pageable = PageRequest.of(page, pageSize);
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return blogRepository.findByStatusOrderByPublishedAtDesc(pageRequest,true);
    }

    //2. Tìm kiếm blog
    public List<BlogPublic> searchBlog(String term) {
        return blogRepository.findByTitleContainsIgnoreCaseAndStatusOrderByPublishedAtDesc(term,true)
                .stream().map(BlogPublic::of).toList();
    }

    //3. Lấy danh sách category
    public List<CategoryWebPublic> getAllCategory() {
        //find -> mapper to CategoryDTO;
        List<Category> categoryList = categoryRepository.findAll();
        return categoryList.stream()
                .map(CategoryWebPublic::of)
                .filter(categoryWebPublic -> categoryWebPublic.getUsed() > 0) //chỉ lấy category được dùng
                .toList();
    }

    //4. Lấy danh sách category được sử dụng nhiều nhất
    public List<CategoryWebPublic> getTop5Category() {
        List<CategoryWebPublic> categoryWebPublicList = getAllCategory();
        return categoryWebPublicList.stream()
                .sorted((c1,c2) -> (c2.getUsed()-c1.getUsed()))
                .limit(5)
                .toList();
    }

    //5. Lấy danh sách bài viết áp dụng category
    public List<BlogPublic> getBlogsInCategory(String category) {
        return blogRepository.findByCategories_NameAndStatusOrderByPublishedAtDesc(category,true)
                .stream().map(BlogPublic::of).toList();
    }

    //6. Lấy chi tiết bài viết
    public BlogPublic getBlogDetail(Integer blogId, String blogSlug) {
        Blog blog = blogRepository.findByIdAndSlugAndStatus(blogId, blogSlug,true)
                .orElseThrow(() -> {
                    throw new NotFoundException("Not found blog");
                });
        return BlogPublic.of(blog);
    }
}
