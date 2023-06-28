package com.example.greenshopcommon.dto.productDto;

import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequestDto {

    private int id;
    private String name;
    private  double price;
    private String description;
    private String image;
    private CategoryDto categoryDto;
    private int quantity;
    private Double rating;
}
