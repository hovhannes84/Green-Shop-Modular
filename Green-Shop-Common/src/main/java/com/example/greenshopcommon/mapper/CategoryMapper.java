package com.example.greenshopcommon.mapper;


import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.categoryDto.CreateCategoryRequestDto;
import com.example.greenshopcommon.dto.categoryDto.UpdateCategoryRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.entity.Category;
import com.example.greenshopcommon.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category map(CreateCategoryRequestDto dto);
    CategoryDto mapToDto(Category entity);
    UpdateCategoryRequestDto updateDto(Category entity);
    Category dtoToMap(CategoryDto entity);

}
