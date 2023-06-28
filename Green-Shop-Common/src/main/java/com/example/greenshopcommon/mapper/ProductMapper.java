package com.example.greenshopcommon.mapper;


import com.example.greenshopcommon.dto.productDto.CreateProductRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.productDto.UpdateProductRequestDto;
import com.example.greenshopcommon.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product map(CreateProductRequestDto dto);
    ProductDto mapToDto(Product entity);
    Product updateDto(UpdateProductRequestDto entity);
    Product dtoToMap(ProductDto entity);


}
