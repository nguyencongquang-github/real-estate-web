package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Images;
import com.example.demo.entity.Listings;
import com.example.demo.entity.User;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.ImagesRepository;
import com.example.demo.repository.ListingsRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ListingsServiceImpl implements ListingsService {
    private final ListingsRepository listingsRepository;
    private final ImagesRepository imagesRepository;
    private final AwsS3Service awsS3Service;
    private final UserRepository userRepository;


    @Override
    public List<ListingsResponseDTO> getAllListings() {
        List<Listings> listings = listingsRepository.findAll();
        List<ListingsResponseDTO> listingsList = new ArrayList<>();

        for (Listings listing : listings) {
            List<Images> imagesList = imagesRepository.findByListingsId(listing.getId());
            List<ImagesResponseDTO> images = new ArrayList<>();
            for (Images image : imagesList) {
                ImagesResponseDTO imagesResponseDTO = ImagesResponseDTO.builder()
                        .id(image.getId())
                        .imageUrl(image.getImageUrl())
                        .build();

                images.add(imagesResponseDTO);
            }

            ListingsResponseDTO listingsResponseDTO = ListingsResponseDTO.builder()
                    .id(listing.getId())
                    .name(listing.getName())
                    .price(listing.getPrice())
                    .area(listing.getArea())
                    .address(listing.getAddress())
                    .description(listing.getDescription())
                    .type(listing.getType())
                    .transactionType(listing.getTransactionType())
                    .status(listing.getStatus())
                    .images(images)
                    .build();

            listingsList.add(listingsResponseDTO);
        }

        return listingsList;
    }

    @Override
    public ListingsResponseDTO getListingsById(int id) {
        Listings listings = listingsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listings not found"));

        List<Images> imagesList = imagesRepository.findByListingsId(listings.getId());
        List<ImagesResponseDTO> images = new ArrayList<>();
        for (Images image : imagesList) {
            ImagesResponseDTO imagesResponseDTO = ImagesResponseDTO.builder()
                    .id(image.getId())
                    .imageUrl(image.getImageUrl())
                    .build();

            images.add(imagesResponseDTO);
        }


        return ListingsResponseDTO.builder()
                .id(listings.getId())
                .name(listings.getName())
                .price(listings.getPrice())
                .area(listings.getArea())
                .address(listings.getAddress())
                .description(listings.getDescription())
                .type(listings.getType())
                .transactionType(listings.getTransactionType())
                .status(listings.getStatus())
                .images(images)
                .build();
    }

    @Override
    public ListingsResponseDTO updateListingsRealtor(int id, ListingsDTO listingsDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();


        Listings listings = listingsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listings not found"));
        if (listingsDTO == null) {
            return null;
        }

        listings.setName(listingsDTO.getName());
        listings.setAddress(listingsDTO.getAddress());
        listings.setArea(listingsDTO.getArea());
        listings.setDescription(listingsDTO.getDescription());
        listings.setPrice(listingsDTO.getPrice());
        listings.setType(listingsDTO.getType());
        listings.setTransactionType(listingsDTO.getTransactionType());
        listings.setStatus(listingsDTO.getStatus());
        listings.setUser(user);


        // xóa image hiện tại có trong listings
        imagesRepository.deleteByListingsId(listings.getId());

        List<Images> imagesList = new ArrayList<>();

        for (MultipartFile img: listingsDTO.getImages()) {
            try {
                String imageUrl = awsS3Service.uploadFile(img);
                Images image = new Images();
                image.setImageUrl(imageUrl);
                image.setListings(listings);
                imagesList.add(image);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        listings.setImages(imagesList);
        Listings savedListings = listingsRepository.save(listings);

        List<ImagesResponseDTO> imagesResponseDTOS = new ArrayList<>();
        for (Images image : imagesList) {
            ImagesResponseDTO imagesResponseDTO = ImagesResponseDTO.builder()
                    .id(image.getId())
                    .imageUrl(image.getImageUrl())
                    .build();

            imagesResponseDTOS.add(imagesResponseDTO);
        }

        return ListingsResponseDTO.builder()
                .id(savedListings.getId())
                .name(savedListings.getName())
                .price(savedListings.getPrice())
                .area(savedListings.getArea())
                .address(savedListings.getAddress())
                .description(savedListings.getDescription())
                .type(savedListings.getType())
                .transactionType(savedListings.getTransactionType())
                .user(savedListings.getUser())
                .status(savedListings.getStatus())
                .images(imagesResponseDTOS)
                .build();
    }

    @Override
    public ListingsResponseDTO deleteListings(int id) {
        Listings listings = listingsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listings not found"));

        List<Images> imagesList = imagesRepository.findByListingsId(listings.getId());
        List<ImagesResponseDTO> images = new ArrayList<>();
        for (Images image : imagesList) {
            ImagesResponseDTO imagesResponseDTO = ImagesResponseDTO.builder()
                    .id(image.getId())
                    .imageUrl(image.getImageUrl())
                    .build();

            images.add(imagesResponseDTO);
        }

        ListingsResponseDTO listingsResponseDTO = ListingsResponseDTO.builder()
                .id(listings.getId())
                .name(listings.getName())
                .price(listings.getPrice())
                .area(listings.getArea())
                .description(listings.getDescription())
                .address(listings.getAddress())
                .type(listings.getType())
                .transactionType(listings.getTransactionType())
                .user(listings.getUser())
                .status(listings.getStatus())
                .images(images)
                .build();

        listingsRepository.delete(listings);
        return listingsResponseDTO;
    }

    @Override
    public ListingsResponseDTO addListings(ListingsDTO listingsDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Listings listings = new Listings();
        listings.setName(listingsDTO.getName());
        listings.setAddress(listingsDTO.getAddress());
        listings.setArea(listingsDTO.getArea());
        listings.setDescription(listingsDTO.getDescription());
        listings.setPrice(listingsDTO.getPrice());
        listings.setType(listingsDTO.getType());
        listings.setTransactionType(listingsDTO.getTransactionType());
        listings.setStatus(listingsDTO.getStatus());
        listings.setUser(user);

        Listings savedListings = listingsRepository.save(listings);
        List<Images> imagesList = new ArrayList<>();

        for (MultipartFile img: listingsDTO.getImages()) {
            try {
                String imageUrl = awsS3Service.uploadFile(img);
                Images image = new Images();
                image.setImageUrl(imageUrl);
                image.setListings(savedListings);
                imagesList.add(image);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        imagesRepository.saveAll(imagesList);

        // Set kieu tra ve
        List<ImagesResponseDTO> imagesResponseDTOs = new ArrayList<>();
        for (Images image : imagesList) {
            ImagesResponseDTO imagesResponseDTO = ImagesResponseDTO.builder()
                    .id(image.getId())
                    .imageUrl(image.getImageUrl())
                    .build();
            imagesResponseDTOs.add(imagesResponseDTO);
        }


        return ListingsResponseDTO.builder()
                .id(savedListings.getId())
                .name(savedListings.getName())
                .address(savedListings.getAddress())
                .price(savedListings.getPrice())
                .area(savedListings.getArea())
                .description(savedListings.getDescription())
                .type(savedListings.getType())
                .transactionType(savedListings.getTransactionType())
                .user(savedListings.getUser())
                .status(savedListings.getStatus())
                .images(imagesResponseDTOs)
                .build();
    }

    @Override
    public Page<Listings> filterListings(String transactionType, String type, String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return listingsRepository.findListingsByCriteria(transactionType, type, name, pageable);
    }

    @Override
    public List<ListingsResponseDTO> getRealtorListingsById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Listings> listings = listingsRepository.findByUser(user);
        List<ListingsResponseDTO> listingsList = new ArrayList<>();

        for (Listings listing : listings) {
            List<Images> imagesList = imagesRepository.findByListingsId(listing.getId());
            List<ImagesResponseDTO> images = new ArrayList<>();
            for (Images image : imagesList) {
                ImagesResponseDTO imagesResponseDTO = ImagesResponseDTO.builder()
                        .id(image.getId())
                        .imageUrl(image.getImageUrl())
                        .build();

                images.add(imagesResponseDTO);
            }

            ListingsResponseDTO listingsResponseDTO = ListingsResponseDTO.builder()
                    .id(listing.getId())
                    .name(listing.getName())
                    .price(listing.getPrice())
                    .area(listing.getArea())
                    .address(listing.getAddress())
                    .description(listing.getDescription())
                    .type(listing.getType())
                    .transactionType(listing.getTransactionType())
                    .status(listing.getStatus())
                    .user(listing.getUser())
                    .images(images)
                    .build();

            listingsList.add(listingsResponseDTO);

        }

        return listingsList;
    }


}
