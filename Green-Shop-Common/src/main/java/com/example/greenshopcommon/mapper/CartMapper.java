package com.example.greenshopcommon.mapper;


import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.dto.cartDto.CreateCartRequestDto;
import com.example.greenshopcommon.dto.cartDto.UpdateCartRequestDto;
import com.example.greenshopcommon.entity.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {

    Cart map(CreateCartRequestDto dto);
    CartDto mapToDto(Cart entity);
    Cart updateDto(UpdateCartRequestDto entity);
}
