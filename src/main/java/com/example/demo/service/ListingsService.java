package com.example.demo.service;

import com.example.demo.dto.ListingsDTO;
import com.example.demo.dto.ListingsResponseDTO;
import com.example.demo.dto.ListingsUpdateDTO;
import com.example.demo.entity.Listings;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ListingsService {
//    ListingsResponseDTO createListings(Listings listings);

    List<ListingsResponseDTO> getAllListings();

    ListingsResponseDTO getListingsById(int id);

    ListingsResponseDTO updateListingsRealtor(int id, ListingsDTO listingsDTO);

    ListingsResponseDTO deleteListings(int id);

    ListingsResponseDTO addListings(ListingsDTO listingsDTO);

    Page<Listings> filterListings(String transactionType, String type, String name, int page, int size);

    List<ListingsResponseDTO> getRealtorListingsById(int id);

}
