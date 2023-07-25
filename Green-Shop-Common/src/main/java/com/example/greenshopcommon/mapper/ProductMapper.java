package com.example.greenshopcommon.mapper;


import com.example.greenshopcommon.dto.productDto.CreateProductRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.productDto.UpdateProductRequestDto;
import com.example.greenshopcommon.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Value;

@Mapper(componentModel = "spring",uses = CategoryMapper.class)
public abstract class ProductMapper {
    @Value("${site.url}")
    String siteUrl;

    public abstract Product map(CreateProductRequestDto dto);
    @Mapping(target = "categoryDto", source = "category")
    @Mapping(target = "picUrl", expression = "java(entity.getImage() != null ? siteUrl + \"/product/getImage?picName=\" + entity.getImage() : null)")
    public abstract ProductDto mapToDto(Product entity);
    public abstract Product updateDto(UpdateProductRequestDto entity);
    public abstract Product dtoToMap(ProductDto entity);


}
