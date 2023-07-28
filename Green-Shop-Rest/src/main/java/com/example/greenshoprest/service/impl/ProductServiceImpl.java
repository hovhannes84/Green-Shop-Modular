package com.example.greenshoprest.service.impl;


import com.example.greenshopcommon.dto.productDto.CreateProductRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.productDto.UpdateProductRequestDto;
import com.example.greenshopcommon.entity.Category;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.Ratingsreview;
import com.example.greenshopcommon.exception.EntityNotFoundException;
import com.example.greenshopcommon.mapper.CategoryMapper;
import com.example.greenshopcommon.mapper.ProductMapper;
import com.example.greenshopcommon.repository.CategoryRepository;
import com.example.greenshopcommon.repository.ProductRepository;
import com.example.greenshopcommon.repository.RatingsreviewRepository;
import com.example.greenshoprest.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final RatingsreviewRepository ratingsreviewRepository;

    @Value("${green_shop.upload.image.path}")
    private String uploadPath;

    // Fetch all products from the repository and calculate their ratings based on reviews
    @Override
    public ResponseEntity<List<ProductDto>> findProducts() {
        List<ProductDto> productDtos = productRepository.findAll().stream()
                .map(product -> {
                    double rating = calculateProductRating(ratingsreviewRepository.findAllByProductId(product.getId()));
                    product.setRating(rating);
                    ProductDto productDto = productMapper.mapToDto(product);
                    productDto.setCategoryDto(categoryMapper.mapToDto(product.getCategory()));
                    return productDto;
                })
                .collect(Collectors.toList());
        log.info("Fetched {} products", productDtos.size());
        return ResponseEntity.ok(productDtos);
    }
    // Find the product by its ID from the repository
    @Override
    public ResponseEntity<?> findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("The id cannot be 0 or less than 0: " + id);
        }
        log.info("Found product with ID: {}", id);
        Optional<Product> byId = productRepository.findById(id);
        return byId.map(product -> {
            double rating = calculateProductRating(ratingsreviewRepository.findAllByProductId(product.getId()));
            product.setRating(rating);
            ProductDto productDto = productMapper.mapToDto(product);
            productDto.setCategoryDto(categoryMapper.mapToDto(product.getCategory()));
            return ResponseEntity.ok(productDto);
        }).orElseThrow(() -> {
            log.info("Product with ID {} not found", id);
            throw new EntityNotFoundException("Product with ID " + id + " does not exist.");
        });
    }
//    Adds a new product to the database based on the provided CreateProductRequestDto.
    @Override
    public ResponseEntity<?> addProduct(CreateProductRequestDto createProductRequestDto) throws IOException {
        if (createProductRequestDto == null) {
            throw new IllegalArgumentException("createProductRequestDto must not be null.");
        }
        Optional<Category> byId = categoryRepository.findById(createProductRequestDto.getCategoryDto().getId());
        if (!byId.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Product product = productMapper.map(createProductRequestDto);
        product.setCategory(byId.get());
        Product saved = productRepository.save(product);
        log.info("Product with ID {} added successfully", saved.getId());
        return ResponseEntity.ok(productMapper.mapToDto(saved));
    }
//Uploads an image for the specified product with the given productId.
    @Override
    public ResponseEntity<ProductDto> uploadImage(int productId,MultipartFile multipartFile) throws IOException {
        if (productId <= 0) {
            throw new IllegalArgumentException("The id cannot be 0 or less than 0: " + productId);
        }
        if (multipartFile == null) {
            throw new IllegalArgumentException("multipartFile must not be null.");
        }
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!multipartFile.isEmpty() && productOptional.isPresent()) {
            String originalFilename = multipartFile.getOriginalFilename();
            String picName = System.currentTimeMillis() + "_" + originalFilename;
            File file = new File(uploadPath + picName);
            multipartFile.transferTo(file);
            Product product = productOptional.get();
            product.setImage(picName);
            productRepository.save(product);
            ProductDto productDto = productMapper.mapToDto(product);
            return ResponseEntity.ok(productDto);
        }
        log.info("Failed to upload image for product with ID: {}", productId);
        return ResponseEntity.badRequest().build();
    }

//    Retrieves the image data with the specified file name.
    @Override
    public @ResponseBody byte[] getImage(String picName) throws IOException {
        File file = new File(uploadPath + picName);
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            return IOUtils.toByteArray(fis);
        }
        log.info("Image not found: {}", picName);
        return null;
    }
//Deletes the product with the specified ID.
    @Override
    public ResponseEntity<?> deleteById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("The id cannot be 0 or less than 0: " + id);
        }
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> {
                    log.info("Product with ID {} not found for deletion", id);
                    throw new EntityNotFoundException("Product with ID " + id + " does not exist.");
                });
    }
//Updates the product with the specified ID using the provided UpdateProductRequestDto.
    @Override
    public ResponseEntity<?> updateProduct(int id, UpdateProductRequestDto updateProductRequestDto) throws IOException {
        if (id <= 0) {
            throw new IllegalArgumentException("The id cannot be 0 or less than 0: " + id);
        }
        if (updateProductRequestDto == null) {
            throw new IllegalArgumentException("updateProductRequestDto must not be null.");
        }
        Optional<Product> productOptional = productRepository.findById(id);
        log.info("Product with ID {} not found for update", id);
        if (productOptional.isEmpty()) {
            throw new EntityNotFoundException("Product with ID " + id + " does not exist.");
        }
        Optional<Category> categoryOptional = categoryRepository.findById(updateProductRequestDto.getCategoryDto().getId());
        if (categoryOptional.isEmpty()) {
            log.info("Invalid category ID for product update");
            return ResponseEntity.badRequest().body("Invalid category ID");
        }
        Product product = productMapper.updateDto(updateProductRequestDto);
        product.setCategory(categoryMapper.dtoToMap(updateProductRequestDto.getCategoryDto()));
        productRepository.save(product);
        return ResponseEntity.ok(productMapper.mapToDto(product));
    }

    public double calculateProductRating(List<Ratingsreview> ratingsreviews) {
        int totalRatings = ratingsreviews.size();
        double sumRatings = 0.0;
        for (Ratingsreview rating : ratingsreviews) {
            sumRatings += rating.getRating();
        }
        double averageRating = (totalRatings > 0) ? sumRatings / totalRatings : 0.0;
        averageRating = Math.round(averageRating * 100.0) / 100.0;
        return averageRating;
    }
}
