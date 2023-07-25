package com.example.greenshoprest.service;


import com.example.greenshopcommon.dto.productDto.CreateProductRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.productDto.UpdateProductRequestDto;
import com.example.greenshopcommon.entity.Ratingsreview;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ResponseEntity<List<ProductDto>> findProducts();

    ResponseEntity<?> findById(int id);

    ResponseEntity<?> addProduct(CreateProductRequestDto createProductRequestDto) throws IOException;

    ResponseEntity<ProductDto> uploadImage(int productId, MultipartFile multipartFile) throws IOException;

    ResponseEntity<?> deleteById(int id);

    ResponseEntity<?> updateProduct(int id, UpdateProductRequestDto updateProductRequestDto) throws IOException;

    @ResponseBody
    byte[] getImage(String picName) throws IOException;

    double calculateProductRating(List<Ratingsreview> ratingsreviews);
}
