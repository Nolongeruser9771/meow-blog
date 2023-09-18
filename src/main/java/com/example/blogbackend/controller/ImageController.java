package com.example.blogbackend.controller;

import com.example.blogbackend.entity.Image;
import com.example.blogbackend.service.ImageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ImageController {
    @Autowired
    private ImageService imageService;

    //1. Upload image theo user (người thực hiện upload chính là user đang login)
    //POST : api/v1/files
    @Transactional
    @PostMapping("api/v1/files")
    public Image uploadImage(MultipartFile file){
        //userId fixed cứng
        Image newImage = imageService.uploadImage(file);
        return newImage;
    }

    //2. Xem ảnh
    //GET : api/v1/files/{id}
    @GetMapping("api/v1/files/{id}")
    public ResponseEntity<?> readImage(@PathVariable Integer id){
        Image image = imageService.getImageById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getType()))
                .body(image.getData());
    }

    //3. Lấy danh sách ảnh của user đang login
    //GET : api/v1/users/{id}/files
    @GetMapping("api/v1/users/{id}/files")
    public ResponseEntity<?> getImagesByUserId(@PathVariable Integer id){
        List<Image> images = imageService.getImagesByUserId(id);
        return ResponseEntity.ok().body(images);
    }

    //4. Xóa ảnh (nếu không phải ảnh của user upload -> không cho xóa)
    //DELETE : api/v1/files/{id}
    @Transactional
    @DeleteMapping("api/v1/files/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Integer id){
        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}
