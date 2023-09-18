package com.example.blogbackend.utils;

import java.util.List;
import java.util.stream.IntStream;

//Chứa các hàm hữu ích - tái sử dụng được
public class Utils {
    public static List<Integer> createList(Integer size){
        List<Integer> range = IntStream.rangeClosed(1, size)
                .boxed().toList();

        return range;
    }
}
