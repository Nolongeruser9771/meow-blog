package com.example.blogbackend.controller;

import com.example.blogbackend.dto.projection.BlogPublic;
import com.example.blogbackend.dto.projection.CategoryWebPublic;
import com.example.blogbackend.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class WebController {

    @Autowired
    private WebService webService;

    //Login Page
    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication!=null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        return "web/login";
    }

    //1. Lấy danh sách blog
        //GET : /?page={pageValue}&pageSize=${pageSizeValue}
        //return Page<Blog>
    @GetMapping("/")
    public String getAllBlog(@RequestParam(required = false, value = "page", defaultValue = "1") Integer page,
                             @RequestParam(required = false, value = "pageSize", defaultValue = "5") Integer pageSize,
                             Model model) {
        Page<BlogPublic> blogPage = webService.getAllBlog(page-1,pageSize);
        List<CategoryWebPublic> categoryTop5 = webService.getTop5Category();
        model.addAttribute("page", blogPage);
        model.addAttribute("categoryList", categoryTop5);
        model.addAttribute("currentPage", page);
        return "web/main";
    }

    //2. Tìm kiếm blog
        //GET : /search?term={termValue}
        //return List<Blog>
    @GetMapping("/search")
    public String searchBlogs(){
        return "web/search";
    }

    @GetMapping(value = "/search/")
    public ResponseEntity<?> searchBlog(@RequestParam("search") String searchValue) {
        List<BlogPublic> blog = webService.searchBlog(searchValue);
        return ResponseEntity.ok().body(blog);
    }

    //3. Lấy danh sách category
        //GET : /categories
        //return List<CategoryDto>
    @GetMapping("/categories")
    public String getAllCategories(Model model) {
        List<CategoryWebPublic> categories = webService.getAllCategory();
        model.addAttribute("categoryList", categories);
        return "web/tag";
    }

    //5. Lấy danh sách bài viết áp dụng category
        //GET : /category/{categoryName}
        //return List<Blog>
    @GetMapping("category/{categoryName}")
    public String getBlogsInCategory(@PathVariable String categoryName,
                                     Model model) {
        List<BlogPublic> blogs = webService.getBlogsInCategory(categoryName);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("blogList", blogs);
        return "web/tagDetail";
    }

    //6. Lấy chi tiết bài viết
        //GET : /blogs/{blogId}/{blogSlug}
        //return Blog
    @GetMapping("blogs/{blogId}/{blogSlug}")
    public String getBlogsInCategory(@PathVariable Integer blogId,
                                     @PathVariable String blogSlug,
                                     Model model) {
        BlogPublic blog = webService.getBlogDetail(blogId,blogSlug);
        model.addAttribute("blog", blog);
        return "web/blogDetail";
    }
}
