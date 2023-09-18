package com.example.blogbackend.repository;

import com.example.blogbackend.dto.projection.BlogPublic;
import com.example.blogbackend.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Integer> {

    List<Blog> findByTitleContainsIgnoreCaseAndStatusOrderByPublishedAtDesc(String title, Boolean status);

    List<Blog> findByCategories_NameAndStatusOrderByPublishedAtDesc(String category, Boolean status);

    Optional<Blog> findByIdAndSlugAndStatus(Integer id, String slug, Boolean status);

    Page<BlogPublic> findByStatusOrderByPublishedAtDesc(PageRequest pageRequest, boolean b);

    Page<BlogPublic> findByOrderByCreatedAtDesc(Pageable pageable);

    List<BlogPublic> findByUser_EmailOrderByCreatedAtDesc(String email);

    List<Blog> findByCategories_Id(Integer id);
}