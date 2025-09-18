package com.example.demo.service;

import com.example.demo.dto.ImagesDTO;
import com.example.demo.dto.ImagesResponseDTO;
import com.example.demo.entity.Images;

import java.util.List;

public interface ImagesService {
    ImagesResponseDTO addImage(ImagesDTO imagesDTO);

    List<Images> getAllImages();
}
