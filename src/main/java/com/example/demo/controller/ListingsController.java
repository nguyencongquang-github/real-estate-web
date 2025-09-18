package com.example.demo.controller;

import com.example.demo.dto.ListingsDTO;
import com.example.demo.dto.ListingsUpdateDTO;
import com.example.demo.dto.ListingsResponseDTO;
import com.example.demo.entity.Listings;
import com.example.demo.repository.ListingsRepository;
import com.example.demo.service.ListingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingsController {
    private final ListingsService listingsService;

//    @PostMapping
//    public ResponseEntity<ListingsResponseDTO> createListings(@RequestBody Listings listings) {
//        ListingsResponseDTO p = listingsService.createListings(listings);
//        return ResponseEntity.status(HttpStatus.CREATED).body(p);
//    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('REALTOR')")
    public ResponseEntity<ListingsResponseDTO> addListings(
            @RequestPart("listings") ListingsDTO listingsDTO,
            @RequestPart("images") List<MultipartFile> images) {
        listingsDTO.setImages(images);
        ListingsResponseDTO result = listingsService.addListings(listingsDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<List<ListingsResponseDTO>> getAllListings() {
        List<ListingsResponseDTO> listingsList = listingsService.getAllListings();
        return ResponseEntity.ok(listingsList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingsResponseDTO> getListingsById(@PathVariable int id) {
        ListingsResponseDTO listings = listingsService.getListingsById(id);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/realtor/{id}")
    public ResponseEntity<List<ListingsResponseDTO>> getRealtorListingsById(@PathVariable int id) {
        List<ListingsResponseDTO> listings = listingsService.getRealtorListingsById(id);
        return ResponseEntity.ok(listings);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('REALTOR')")
    public ResponseEntity<ListingsResponseDTO> updateListingsRealtor(
            @PathVariable int id,
            @RequestPart("listings") ListingsDTO listingsDTO,
            @RequestPart("images") List<MultipartFile> images) {

        listingsDTO.setImages(images);
        ListingsResponseDTO p = listingsService.updateListingsRealtor(id, listingsDTO);
        return ResponseEntity.ok(p);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('REALTOR')")
    public ResponseEntity<ListingsResponseDTO> deleteListings(@PathVariable int id) {
        ListingsResponseDTO p = listingsService.deleteListings(id);
        return ResponseEntity.ok(p);
    }

    // Filter
    @GetMapping("/filter")
    public Page<Listings> filterListings(
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name,
            @RequestParam("p") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return listingsService.filterListings(transactionType, type, name, page, size);
    }

}
