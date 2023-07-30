package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.CreateRatingsreviewRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.RatingsreviewDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.UpdateRatingsreviewRequestDto;
import com.example.greenshopcommon.entity.Ratingsreview;
import com.example.greenshopcommon.exception.EntityNotFoundException;
import com.example.greenshopcommon.mapper.RatingsreviewMapper;
import com.example.greenshopcommon.repository.ProductRepository;
import com.example.greenshopcommon.repository.RatingsreviewRepository;
import com.example.greenshoprest.security.CurrentUser;
import com.example.greenshoprest.service.ProductService;
import com.example.greenshoprest.service.RatingsreviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingsreviewServiceImpl implements RatingsreviewService {

    private final RatingsreviewRepository ratingsreviewRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final RatingsreviewMapper ratingsreviewMapper;

    // Fetch all ratings reviews by productId and map them to DTOs
    @Override
    public ResponseEntity<List<RatingsreviewDto>> getAllByProductId(int productId) {
        List<RatingsreviewDto> ratingDtoAll = ratingsreviewRepository
                .findAllByProductId(productId)
                .stream()
                .map(ratingsreviewMapper::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ratingDtoAll);
    }

    // Fetch all ratings reviews and map them to DTOs
    @Override
    public ResponseEntity<List<RatingsreviewDto>> getAll() {
        List<RatingsreviewDto> ratingDtoAll = ratingsreviewRepository
                .findAll()
                .stream()
                .map(ratingsreviewMapper::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ratingDtoAll);
    }

    // Create a new ratings review and rating for a product
    @Override
    public ResponseEntity<?> createReviewAndRating(CreateRatingsreviewRequestDto createRatingsreviewRequestDto, CurrentUser currentUser) {
        log.info("Creating a new ratings review and rating");
        return productRepository.findById(createRatingsreviewRequestDto.getProductDto().getId())
                .map(product -> {
                    Ratingsreview ratingsreview = ratingsreviewMapper.map(createRatingsreviewRequestDto);
                    ratingsreview.setProduct(product);
                    ratingsreview.setUser(currentUser.getUser());
                    ratingsreview.setDateTime(new Date());
                    ratingsreviewRepository.save(ratingsreview);
                    RatingsreviewDto ratingsreviewDto = ratingsreviewMapper.mapToDto(ratingsreview);
                    return ResponseEntity.ok(ratingsreviewDto);
                })
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    // Fetch ratings reviews by user ID and map them to DTOs
    @Override
    public ResponseEntity<?> getRatingsreviewByUserId(CurrentUser currentUser) {
        if (currentUser == null) {
            throw new IllegalArgumentException("CurrentUser or its associated User must not be null.");
        }
        log.info("Fetching ratings review for user with ID: {}", currentUser.getUser().getId());
        return Optional.ofNullable(currentUser)
                .map(CurrentUser::getUser)
                .flatMap(user -> ratingsreviewRepository.findRatingsreviewByUserId(user.getId())
                        .map(ratingsreviewMapper::mapToDto))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    log.info("Ratings review not found for user with ID: {}", currentUser.getUser().getId());
                    return new EntityNotFoundException("Ratings review not found for user with ID " + currentUser.getUser().getId() + ".");
                });
    }

    // Update an existing ratings review and rating
    @Override
    public ResponseEntity<?> updateRatingsreview(UpdateRatingsreviewRequestDto updateRatingsreviewRequestDto, CurrentUser currentUser) {
        if (currentUser == null && updateRatingsreviewRequestDto == null) {
            throw new IllegalArgumentException("CurrentUser and updateRatingsreviewRequestDto must not be null.");
        }
        log.info("Updating ratings review with ID: {}", updateRatingsreviewRequestDto.getId());
        return productRepository.findById(updateRatingsreviewRequestDto.getProductDto().getId())
                .map(product -> {
                    Ratingsreview ratingsreview = ratingsreviewMapper.updateDto(updateRatingsreviewRequestDto);
                    ratingsreview.setProduct(product);
                    ratingsreview.setUser(currentUser.getUser());
                    ratingsreview.setDateTime(new Date());
                    ratingsreviewRepository.save(ratingsreview);
                    RatingsreviewDto ratingsreviewDto = ratingsreviewMapper.mapToDto(ratingsreview);
                    return ResponseEntity.ok(ratingsreviewDto);
                })
                .orElseThrow(() -> {
                    log.info("Ratings review with ID {} not found for update", updateRatingsreviewRequestDto.getId());
                    return new EntityNotFoundException("Ratings review with ID " + updateRatingsreviewRequestDto.getId() + " not found.");
                });

    }

    // Delete a ratings review and rating by ID
    @Override
    public ResponseEntity<?> deleteRatingsreview(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("The id cannot be 0 or less than 0: " + id);
        }
        log.info("Deleting ratings review with ID: {}", id);
        return ratingsreviewRepository.findById(id)
                .map(ratingsreview -> {
                    ratingsreviewRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> {
                    log.info("Ratings review with ID {} not found for deletion", id);
                    return new EntityNotFoundException("Ratings review with ID " + id + " not found.");
                });
    }

    // Fetch all products with their associated ratings
    @Override
    public ResponseEntity<List<ProductDto>> allProductsRating() {
        log.info("Fetching all products with ratings");
        List<ProductDto> products = productService.findProducts().getBody();
        List<ProductDto> productsWithRating = products.stream()
                .map(productDto -> {
                    List<Ratingsreview> productReviews = ratingsreviewRepository.findAllByProductId(productDto.getId());
                    List<RatingsreviewDto> ratingsreviewDtos = productReviews.stream()
                            .map(ratingsreviewMapper::mapToDto)
                            .collect(Collectors.toList());
                    double rating = calculateProductRating(ratingsreviewDtos);
                    productDto.setRating(rating);
                    return productDto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(productsWithRating);
    }

    // Helper method to calculate the average rating for a product
    public double calculateProductRating(List<RatingsreviewDto> ratingsreviews) {
        int totalRatings = ratingsreviews.size();
        double sumRatings = 0.0;
        for (RatingsreviewDto rating : ratingsreviews) {
            sumRatings += rating.getRating();
        }
        double averageRating = (totalRatings > 0) ? sumRatings / totalRatings : 0.0;
        averageRating = Math.round(averageRating * 100.0) / 100.0;
        return averageRating;
    }

}