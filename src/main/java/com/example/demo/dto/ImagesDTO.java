package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class ImagesDTO {
    private String imageUrl;
    private int listings_id;
}
