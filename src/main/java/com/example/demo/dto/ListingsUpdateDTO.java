package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ListingsUpdateDTO {
    private String name;
    private String address;
    private String area;
    private String description;
    private double price;
    private String type;
    private String transactionType;
    private String status;

    private List<ImagesDTO> images;
}
