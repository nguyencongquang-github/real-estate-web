package com.example.demo.controller;

import com.example.demo.dto.ImagesDTO;
import com.example.demo.dto.ImagesResponseDTO;
import com.example.demo.entity.Images;
import com.example.demo.service.ImagesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImagesController {
    private final ImagesService imagesService;

    @PostMapping
    public ResponseEntity<ImagesResponseDTO> addImage(@RequestBody ImagesDTO imagesDTO) {
        ImagesResponseDTO img = imagesService.addImage(imagesDTO);
        return ResponseEntity.ok(img);
    }

    @GetMapping
    public ResponseEntity<List<Images>> getAllImages() {
        List<Images> imagesList = imagesService.getAllImages();
        return ResponseEntity.ok(imagesList);
    }
}
