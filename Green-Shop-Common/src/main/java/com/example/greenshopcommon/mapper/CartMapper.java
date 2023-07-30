package com.example.greenshopcommon.mapper;


import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.dto.cartDto.CreateCartRequestDto;
import com.example.greenshopcommon.dto.cartDto.UpdateCartRequestDto;
import com.example.greenshopcommon.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {CategoryMapper.class, ProductMapper.class})
public interface CartMapper {

    Cart map(CreateCartRequestDto dto);
    @Mapping(target = "productDto", source = "product")
    @Mapping(target = "userDto", source = "user")
    CartDto mapToDto(Cart entity);
    Cart updateDto(UpdateCartRequestDto entity);
}
