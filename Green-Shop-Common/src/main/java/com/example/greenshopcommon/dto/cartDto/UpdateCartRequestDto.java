package com.example.greenshopcommon.dto.cartDto;

import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartRequestDto {

    private int id;
    private UserDto userDto;
    private ProductDto productDto;
    private int quantity;

}
