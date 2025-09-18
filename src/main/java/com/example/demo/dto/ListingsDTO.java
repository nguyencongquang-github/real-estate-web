package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Builder
public class ListingsDTO {
    private String name;
    private String address;
    private String area;
    private String description;
    private double price;
    private String type;
    private String transactionType;
    private String status;
    private int realtor_id;
    private List<MultipartFile> images;
}
