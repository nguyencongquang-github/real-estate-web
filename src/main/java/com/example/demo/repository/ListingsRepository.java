package com.example.demo.repository;

import com.example.demo.entity.Listings;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListingsRepository extends JpaRepository<Listings, Integer> {
    @Query("SELECT l FROM Listings l WHERE " +
            "(:transactionType IS NULL OR l.transactionType = :transactionType) AND " +
            "(:type IS NULL OR l.type = :type) AND " +
            "(:name IS NULL OR l.name LIKE %:name%)")
    Page<Listings> findListingsByCriteria(
            @Param("transactionType") String transactionType,
            @Param("type") String type,
            @Param("name") String name,
            Pageable pageable);

    List<Listings> findByUser(User user);
}
