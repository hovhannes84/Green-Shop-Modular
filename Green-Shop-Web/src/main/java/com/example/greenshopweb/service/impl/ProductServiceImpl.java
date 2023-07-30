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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Value("${green-shop.upload.image.path}")
    private String imageUploadPath;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;


    // Retrieves all products from the database and returns a list of ProductDto objects.
    @Override
    public List<ProductDto> findProducts() {
        log.info("Retrieving all products.");
        List<Product> products = productRepository.findAll();
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = productMapper.mapToDto(product);
            productDto.setCategoryDto(categoryMapper.mapToDto(product.getCategory()));
            productDtos.add(productDto);
        }
        return productDtos;
    }


    // Retrieves a product by its ID from the database and returns an Optional<Product>.
    @Override
    public Optional<Product> findById(int id) {
        log.info("Retrieving product with ID: {}", id);
        return productRepository.findById(id);
    }


    // Adds a new product to the database using the information from CreateProductRequestDto.
    @Override
    public void addProduct(User currentUser, MultipartFile multipartFile, @Valid CreateProductRequestDto createProductRequestDto) throws IOException {
        log.info("Adding a new product.");
        validateProductRequestDto(createProductRequestDto);
        String fileName = System.nanoTime() + "_" + multipartFile.getOriginalFilename();
        File file = new File(imageUploadPath + fileName);
        multipartFile.transferTo(file);
        createProductRequestDto.setImage(fileName);
        productRepository.save(productMapper.map(createProductRequestDto));
        log.info("New product added successfully.");
    }

    // Deletes a product from the database by its ID.
    @Override
    public void deleteById(int id) {
        log.info("Deleting product with ID: {}", id);
        productRepository.deleteById(id);
        log.info("Product with ID {} deleted successfully.", id);
    }

    // Updates an existing product in the database using the information from UpdateProductRequestDto.
    @Override
    public void updateProduct(User currentUser, MultipartFile multipartFile, @Valid UpdateProductRequestDto updateProductRequestDto) throws IOException {
        log.info("Updating product with ID: {}", updateProductRequestDto.getId());

        Product product = productMapper.updateDto(updateProductRequestDto);
        Optional<Product> existingProductOptional = productRepository.findById(product.getId());

        if (existingProductOptional.isPresent()) {
            Product existingProduct = existingProductOptional.get();

            try {
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
                log.info("Product updated successfully.");
            } catch (IOException e) {
                log.error("Failed to update product with ID: {}", updateProductRequestDto.getId());
                throw new IOException("Failed to update product with ID: " + updateProductRequestDto.getId(), e);
            } catch (Exception e) {
                log.error("An unexpected error occurred while updating product with ID: {}", updateProductRequestDto.getId());
                throw new RuntimeException("An unexpected error occurred while updating product with ID: " + updateProductRequestDto.getId(), e);
            }
        } else {
            throw new EntityNotFoundException("Product with ID " + updateProductRequestDto.getId() + " not found.");
        }
    }


    // Retrieves a single product by its ID from the database and returns the corresponding ProductDto.
    public ProductDto singleProduct(int id) {
        log.info("Retrieving single product with ID: {}", id);
        Optional<Product> byId = productRepository.findById(id);
        if (byId.isPresent()) {
            log.info("Product with ID {} found.", id);
            return productMapper.mapToDto(byId.get());
        }
        log.info("Product with ID {} not found.", id);
        return null;
    }

    private void validateProductRequestDto(@NotNull CreateProductRequestDto createProductRequestDto) {
        if (createProductRequestDto.getName() == null || createProductRequestDto.getName().isEmpty()) {
            throw new ValidationException("Product name cannot be empty.");
        }
        if (createProductRequestDto.getDescription() == null || createProductRequestDto.getDescription().isEmpty()) {
            throw new ValidationException("Product description cannot be empty.");
        }
    }
}