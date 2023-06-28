package com.example.greenshopweb.service.impl;


import com.example.greenshopcommon.dto.productDto.CreateProductRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.productDto.UpdateProductRequestDto;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.mapper.CategoryMapper;
import com.example.greenshopcommon.mapper.ProductMapper;
import com.example.greenshopcommon.repository.ProductRepository;
import com.example.greenshopweb.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    @Value("${green-shop.upload.image.path}")
    private String imageUploadPath;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public List<ProductDto> findProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = productMapper.mapToDto(product);
            productDto.setCategoryDto(categoryMapper.mapToDto(product.getCategory()));
            productDtos.add(productDto);
        }
        return productDtos;
    }

    @Override
    public Optional<Product> findById(int id) {
        return productRepository.findById(id);
    }

    @Override
    public void addProduct(User currentUser, MultipartFile multipartFile, CreateProductRequestDto createProductRequestDto) throws IOException {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
            File file = new File(imageUploadPath + fileName);
            multipartFile.transferTo(file);
            createProductRequestDto.setImage(fileName);
        }
        productRepository.save(productMapper.map(createProductRequestDto));
    }

    @Override
    public void deleteById(int id) {
        productRepository.deleteById(id);
    }

    @Override
    public void updateProduct(User currentUser, MultipartFile multipartFile, UpdateProductRequestDto updateProductRequestDto) throws IOException {
        Product product = productMapper.updateDto(updateProductRequestDto);
        Optional<Product> existingProductOptional = productRepository.findById(product.getId());

        if (existingProductOptional.isPresent()) {
            Product existingProduct = existingProductOptional.get();
            if (product.getName() != null) {
                existingProduct.setName(product.getName());
            }
            if (product.getDescription() != null) {
                existingProduct.setDescription(product.getDescription());
            }
            if (multipartFile != null && !multipartFile.isEmpty()) {
                String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
                File file = new File(imageUploadPath + fileName);
                multipartFile.transferTo(file);
                existingProduct.setImage(fileName);
            }
            productRepository.saveAndFlush(existingProduct);
        }

    }
    public ProductDto singleProduct(int id){
        Optional<Product> byId = productRepository.findById(id);
        if (byId.isPresent()) {
            return productMapper.mapToDto(byId.get());
        }return null;

    }

}
