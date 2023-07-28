package com.example.greenshopcommon.dto.cartDto;

import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCartRequestDto {

    private int id;

    @Valid
    private UserDto userDto;

    @Valid
    private ProductDto productDto;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

}
