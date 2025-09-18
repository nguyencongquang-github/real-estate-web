package com.example.demo.service;

import com.example.demo.dto.ImagesDTO;
import com.example.demo.dto.ImagesResponseDTO;
import com.example.demo.entity.Images;
import com.example.demo.entity.Listings;
import com.example.demo.repository.ImagesRepository;
import com.example.demo.repository.ListingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImagesServiceImpl implements ImagesService {
    private final ImagesRepository imagesRepository;
    private final ListingsRepository listingsRepository;

    @Override
    public ImagesResponseDTO addImage(ImagesDTO imagesDTO) {
        Listings listings = listingsRepository.findById(imagesDTO.getListings_id())
                .orElseThrow(() -> new RuntimeException("Listings not found"));

        Images images = new Images();
        images.setListings(listings);
        images.setImageUrl(imagesDTO.getImageUrl());
        images = imagesRepository.save(images);


        return ImagesResponseDTO.builder()
                .id(images.getId())
                .imageUrl(images.getImageUrl())
                .build();
    }

    @Override
    public List<Images> getAllImages() {
        return imagesRepository.findAll();
    }
}
