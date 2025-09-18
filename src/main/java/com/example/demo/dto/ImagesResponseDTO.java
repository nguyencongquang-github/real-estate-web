package com.example.demo.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagesResponseDTO {
    private int id;
    private String imageUrl;
}
