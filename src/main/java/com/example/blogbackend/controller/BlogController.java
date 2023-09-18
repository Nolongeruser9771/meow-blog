package com.example.blogbackend.controller;

import com.example.blogbackend.dto.projection.BlogPublic;
import com.example.blogbackend.dto.projection.CategoryPublic;
import com.example.blogbackend.entity.User;
import com.example.blogbackend.request.UpsertBlogRequest;
import com.example.blogbackend.service.BlogService;
import com.example.blogbackend.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
public class BlogController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private CategoryService categoryService;

    //1. Lấy danh sách blog (có phân trang, mặc đinh pageSize = 10)
    @GetMapping("/admin/blogs")
    public String getAllBlogsPage(@RequestParam(defaultValue = "1", required = false) Integer page,
                                  @RequestParam(defaultValue = "10", required = false) Integer pageSize,
                                  Model model) {
        Page<BlogPublic> blogList = blogService.getBlogPage(page-1, pageSize);
        model.addAttribute("page", blogList);
        model.addAttribute("currentPage", page);
        return "admin/blog/blog-index";
    }

    //2. Lấy danh sách blog user đang login
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    @GetMapping("/admin/blogs/own-blogs")
    public String getOwnBlogsPage(Model model, HttpSession session) {
        //Lấy thông tin user từ session
        String email = (String) session.getAttribute("SESSION");
        log.info("------------------------");
        log.info("user email = " + email);

        //user_id fixed to 1
        List<BlogPublic> blogList = blogService.getAllOwnBlog(email);
        model.addAttribute("blogList", blogList);
        return "admin/blog/own-blog";
    }

    //3. Lấy chi tiết blog theo blog id
    //GET : admin/blogs/{id}/detail (Trả về Giao diện)
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    @GetMapping("admin/blogs/{id}/detail")
    public String getBlogById(@PathVariable Integer id,
                              Model model) {
        //Trả về blog và category của blog
        BlogPublic blog = blogService.getBlogDetail(id);
        List<CategoryPublic> categoryList = categoryService.getAllCategory();

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("blog", blog);
        return "admin/blog/blog-detail";
    }

    //4. Thêm blog mới
    //POST : api/v1/admin/blogs/create
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    @PostMapping("api/v1/admin/blogs/create")
    public ResponseEntity<?> addBlog(@RequestBody UpsertBlogRequest request, HttpSession session) {
        //Lấy thông tin user từ session
        String email = (String) session.getAttribute("SESSION");

        BlogPublic newBlog = blogService.addNewBlog(request, email);
        return ResponseEntity.ok().body(newBlog);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    @GetMapping("admin/blogs/create")
    public String createBlog(Model model) {
        List<CategoryPublic> categoryList = categoryService.getAllCategory();
        model.addAttribute("categoryList", categoryList);
        return "admin/blog/blog-create";
    }

    //5. Cập nhật blog
    //PUT : api/v1/admin/blogs/{id}
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    @PutMapping("api/v1/admin/blogs/{id}")
    public ResponseEntity<?> updateBlog(@PathVariable Integer id, @RequestBody UpsertBlogRequest request) {
        BlogPublic updatedBlog = blogService.updateBlog(id, request);
        return ResponseEntity.ok().body(updatedBlog);
    }

    //6. Xóa blog (xóa luôn comment liên quan)
    //DELETE : api/v1/admin/blogs/{id}
    @PreAuthorize("hasRole('ADMIN') or hasRole('AUTHOR')")
    @DeleteMapping("api/v1/admin/blogs/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable Integer id) {
        blogService.deleteBlog(id);
        return ResponseEntity.noContent().build();
    }
}
