package com.example.blogbackend.service;

import com.example.blogbackend.dto.projection.CategoryPublic;
import com.example.blogbackend.dto.projection.CategoryWebPublic;
import com.example.blogbackend.entity.Blog;
import com.example.blogbackend.entity.Category;
import com.example.blogbackend.exception.BadRequestException;
import com.example.blogbackend.exception.NotFoundException;
import com.example.blogbackend.repository.BlogRepository;
import com.example.blogbackend.repository.CategoryRepository;
import com.example.blogbackend.request.UpsertCategoryRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BlogRepository blogRepository;

    //Danh sách Category List
    public List<CategoryPublic> getAllCategory() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryList.stream()
                .map(CategoryPublic::of)
                .toList();
    }
    //Danh sách Category phân trang
    public Page<CategoryPublic> getCategoryPage(Integer page, Integer pageSize){
        Pageable pageable = PageRequest.of(page, pageSize);
        return categoryRepository.findByOrderByIdAsc(pageable);
    }

    private void isCategoryNameDuplicated(String name){
        if (categoryRepository.existsByName(name)) {
            throw new BadRequestException("Category name duplicated");
        }
    }
    private Category findCategoryById(Integer id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                ()-> { throw new NotFoundException("Category id "+ id +" not found");}
        );
        return category;
    }
    //Lấy số lượng blog sử dụng category
    public Integer getUsedOfCategory(Integer categoryId) {
        //Tìm category
        Category category = findCategoryById(categoryId);
        return Math.toIntExact(category.getBlogs().stream()
                .filter(Blog::getStatus)
                .count());
    }

    //Thêm category (tên không trùng nhau)
    @Transactional
    public CategoryPublic addCategory(UpsertCategoryRequest request){
        //category name duplicated?
        isCategoryNameDuplicated(request.getName());

        Category newCategory = Category.builder()
                .name(request.getName())
                .build();
        categoryRepository.save(newCategory);
        return CategoryPublic.of(newCategory);
    }

    //Cập nhật category (tên không trùng nhau)
    @Transactional
    public CategoryWebPublic updateCategory(Integer categoryId, UpsertCategoryRequest request){
        //category id exist?
        Category category2update = findCategoryById(categoryId);

        //category name duplicated?
        isCategoryNameDuplicated(request.getName());

        //find blog list
        List<Blog> blogList = blogRepository.findByCategories_Id(categoryId);

        category2update.setName(request.getName());
        category2update.setBlogs(blogList);

        return CategoryWebPublic.of(category2update);
    }
    //Xóa category (xóa blog áp dụng category, ko xóa blog trong bảng blog)
    @Transactional
    public void deleteCategory(Integer categoryId){
        //find blog list
        List<Blog> blogList = blogRepository.findByCategories_Id(categoryId);
        if (blogList.size()!=0) {
            throw new BadRequestException("Category is in use");
        } else {
            Category category2delete = findCategoryById(categoryId);
            categoryRepository.delete(category2delete);
        }
    }
}
