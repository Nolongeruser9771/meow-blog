package com.example.blogbackend.service;

import com.example.blogbackend.entity.Image;
import com.example.blogbackend.entity.User;
import com.example.blogbackend.exception.FileHandleException;
import com.example.blogbackend.exception.NotFoundException;
import com.example.blogbackend.repository.ImageRepository;
import com.example.blogbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    //Upload image
    @Transactional
    public Image uploadImage(MultipartFile file){
        //Validate file (if needed)

        //Kiểm tra userid có tồn tại ko (fixed to 1)
        User user = userRepository.findById(2).orElseThrow(
                () -> {throw new NotFoundException("User id 2 not found");}
        );

        //tạo new Image -> lưu vào repo
        try {
            Image image =  Image.builder()
                    .data(file.getBytes())
                    .type(file.getContentType())
                    .user(user).build();
            imageRepository.save(image);
            return image;
        }catch (IOException e) {
            throw new FileHandleException("Error when uploading image...");
        }
    }

    //Xem ảnh (getImageById)
    public Image getImageById(Integer imageId){
        return imageRepository.findById(imageId).orElseThrow(
                ()->{throw new NotFoundException("Image id "+ imageId+" not found");}
        );
    }

    //Lấy danh sách ảnh user đang login (fixed to 1) (getImagesByUserId)
    public List<Image> getImagesByUserId(Integer userId){
        //Kiểm tra tồn tại userId hay không
        userId = 2;

        //userId fixed to 1
        return imageRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    //Xóa ảnh (nếu ko phải ảnh của user upload -> không cho xóa)
    //find user - user == this.user ->  delete
    @Transactional
    public void deleteImage(Integer imageId){
        Image image2delete = getImageById(imageId);
        imageRepository.delete(image2delete);
    }
}
