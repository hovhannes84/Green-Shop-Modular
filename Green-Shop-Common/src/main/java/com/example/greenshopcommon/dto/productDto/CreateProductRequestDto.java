package com.example.greenshopcommon.dto.productDto;

import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequestDto {

    private int id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name can have a maximum of 100 characters")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive value")
    private double price;

    @Size(max = 1000, message = "Description can have a maximum of 1000 characters")
    private String description;

    private String image;

    @NotNull(message = "Category is required")
    private CategoryDto categoryDto;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive value")
    private int quantity;
    private Double rating;
}
