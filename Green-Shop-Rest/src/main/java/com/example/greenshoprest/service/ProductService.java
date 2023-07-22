package com.example.greenshoprest.service;


import com.example.greenshopcommon.dto.productDto.CreateProductRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.productDto.UpdateProductRequestDto;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    ResponseEntity<List<ProductDto>> findProducts();
    Optional<Product> findById(int id);
    ResponseEntity<?> addProduct(User currentUser, MultipartFile multipartFile, CreateProductRequestDto createProductRequestDto) throws IOException;
    ResponseEntity<?> deleteById(int id);
    ResponseEntity<?> updateProduct(User currentUser, MultipartFile multipartFile, UpdateProductRequestDto updateProductRequestDto) throws IOException;
    ResponseEntity<ProductDto> singleProduct(int id);
}
