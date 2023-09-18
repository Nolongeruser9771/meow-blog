package com.example.blogbackend.controller;

import com.example.blogbackend.exception.BadRequestException;
import com.example.blogbackend.exception.ErrorMessage;
import com.example.blogbackend.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandleController {

    @ExceptionHandler(NotFoundException.class)
    public ErrorMessage NotFoundExceptionHandler(NotFoundException ex) {
        return new ErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ErrorMessage Exception(Exception ex) {
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ErrorMessage Exception(BadRequestException ex) {
        return new ErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
