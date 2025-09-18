package com.example.demo.dto;

import com.example.demo.dto.user.UserResponseDTO;
import com.example.demo.entity.User;
import lombok.*;

import java.util.List;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingsResponseDTO {
    private int id;
    private String name;
    private String address;
    private String area;
    private String description;
    private double price;
    private String type;
    private String transactionType;
    private String status;
    private User user;
    private List<ImagesResponseDTO> images;
}
