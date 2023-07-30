package com.example.greenshopcommon.dto.cartDto;

import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {

    private int id;

    @Valid
    private UserDto userDto;

    @Valid
    private ProductDto productDto;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

}
