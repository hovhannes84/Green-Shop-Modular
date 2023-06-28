package com.example.greenshopweb.service;


import com.example.greenshopcommon.dto.productDto.CreateProductRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.productDto.UpdateProductRequestDto;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductDto> findProducts();
    Optional<Product> findById(int id);
    void addProduct(User currentUser, MultipartFile multipartFile, CreateProductRequestDto createProductRequestDto) throws IOException;
    void deleteById(int id);
    public void updateProduct(User currentUser, MultipartFile multipartFile, UpdateProductRequestDto updateProductRequestDto) throws IOException;
    public ProductDto singleProduct(int id);
}
