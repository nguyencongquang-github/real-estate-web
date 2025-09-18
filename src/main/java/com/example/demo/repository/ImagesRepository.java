package com.example.demo.repository;

import com.example.demo.entity.Images;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagesRepository extends JpaRepository<Images, Integer> {
    List<Images> findByListingsId(int listingId);
    void deleteByListingsId(int listingId);
}
