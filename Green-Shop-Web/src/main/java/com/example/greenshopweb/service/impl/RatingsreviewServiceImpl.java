package com.example.greenshopweb.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.CreateRatingsreviewRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.RatingsreviewDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.UpdateRatingsreviewRequestDto;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.Ratingsreview;
import com.example.greenshopcommon.mapper.RatingsreviewMapper;
import com.example.greenshopcommon.repository.RatingsreviewRepository;
import com.example.greenshopweb.security.CurrentUser;
import com.example.greenshopweb.service.ProductService;
import com.example.greenshopweb.service.RatingsreviewService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class RatingsreviewServiceImpl implements RatingsreviewService {


    private final RatingsreviewRepository ratingsreviewRepository;
    private final ProductService productService;
    private final RatingsreviewMapper ratingsreviewMapper;


    // Method to get all ratings and reviews for a specific product by its ID
    @Override
    public List<RatingsreviewDto> getAllByProductId(int productId) {
        log.info("Getting all ratings and reviews for product ID: {}", productId);

        List<Ratingsreview> all = ratingsreviewRepository.findAllByProductId(productId);
        List<RatingsreviewDto> ratingDtoAll = new ArrayList<>();
        for (Ratingsreview ratingsreview : all) {
            RatingsreviewDto ratingsreviewDto = ratingsreviewMapper.mapToDto(ratingsreview);
            ratingDtoAll.add(ratingsreviewDto);
        }
        return ratingDtoAll;
    }

    // Method to get all ratings and reviews for all products
    @Override
    public List<RatingsreviewDto> getAll() {
        log.info("Getting all ratings and reviews");

        List<Ratingsreview> all = ratingsreviewRepository.findAll();
        List<RatingsreviewDto> ratingDtoAll = new ArrayList<>();
        for (Ratingsreview ratingsreview : all) {
            RatingsreviewDto ratingsreviewDto = ratingsreviewMapper.mapToDto(ratingsreview);
            ratingDtoAll.add(ratingsreviewDto);
        }
        return ratingDtoAll;
    }

    // Method to create a new review and rating for a product
    @Override
    public void createReviewAndRating(CreateRatingsreviewRequestDto createRatingsreviewRequestDto, CurrentUser currentUser) {
        try {
            log.info("Creating new review and rating for user {}", currentUser.getUser().getId());

            if (createRatingsreviewRequestDto == null) {
                throw new IllegalArgumentException("Invalid request data. Rating and review data is missing.");
            }
            ProductDto productDto = createRatingsreviewRequestDto.getProductDto();
            if (productDto == null || productDto.getId() <= 0) {
                throw new IllegalArgumentException("Invalid product ID provided in the request.");
            }
            Optional<Product> productOptional = productService.findById(productDto.getId());
            if (productOptional.isEmpty()) {
                throw new IllegalArgumentException("Product with the given ID does not exist.");
            }
            Product product = productOptional.get();
            Ratingsreview ratingsreview = ratingsreviewMapper.map(createRatingsreviewRequestDto);
            ratingsreview.setProduct(product);
            ratingsreview.setUser(currentUser.getUser());
            ratingsreviewRepository.save(ratingsreview);

            log.info("Review and rating created successfully for product ID: {}", productDto.getId());
        } catch (IllegalArgumentException ex) {
            log.error("Error while creating review and rating: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while creating review and rating", ex);
            throw new RuntimeException("Unexpected error while creating review and rating", ex);
        }
    }

    // Method to get the ratings and reviews submitted by a specific user
    @Override
    public Ratingsreview getRatingsreviewByUserId(CurrentUser currentUser) {
        log.info("Getting ratings and review for user {}", currentUser.getUser().getId());

        int userId = currentUser.getUser().getId();
        Optional<Ratingsreview> ratingsreview = ratingsreviewRepository.findRatingsreviewByUserId(userId);
        return ratingsreview.orElse(null);
    }

    // Method to update an existing ratings and review for a product
    @Override
    public void updateRatingsreview(UpdateRatingsreviewRequestDto updateRatingsreviewRequestDto, CurrentUser currentUser) {
        try {
            log.info("Updating review and rating for user {}", currentUser.getUser().getId());

            if (updateRatingsreviewRequestDto == null) {
                throw new IllegalArgumentException("Invalid request data. Updated rating and review data is missing.");
            }
            ProductDto productDto = updateRatingsreviewRequestDto.getProductDto();
            if (productDto == null || productDto.getId() <= 0) {
                throw new IllegalArgumentException("Invalid product ID provided in the request.");
            }
            Optional<Product> productOptional = productService.findById(productDto.getId());
            if (productOptional.isEmpty()) {
                throw new IllegalArgumentException("Product with the given ID does not exist.");
            }
            Product product = productOptional.get();
            Ratingsreview ratingsreview = ratingsreviewMapper.updateDto(updateRatingsreviewRequestDto);
            ratingsreview.setProduct(product);
            ratingsreview.setUser(currentUser.getUser());
            ratingsreviewRepository.save(ratingsreview);

            log.info("Review and rating updated successfully for product ID: {}", productDto.getId());
        } catch (IllegalArgumentException ex) {
            log.error("Error while updating review and rating: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while updating review and rating", ex);
            throw new RuntimeException("Unexpected error while updating review and rating", ex);
        }
    }

    // Method to delete a ratings and review by its ID
    @Override
    public void deleteRatingsreview(int id) {
        try {
            log.info("Deleting review and rating with ID: {}", id);

            if (id <= 0) {
                throw new IllegalArgumentException("Invalid ratings review ID provided.");
            }
            Optional<Ratingsreview> ratingsreviewOptional = ratingsreviewRepository.findById(id);
            if (ratingsreviewOptional.isEmpty()) {
                throw new IllegalArgumentException("Ratings review with the given ID does not exist.");
            }
            ratingsreviewOptional.ifPresent(ratingsreviewRepository::delete);

            log.info("Review and rating deleted successfully for ID: {}", id);
        } catch (IllegalArgumentException ex) {
            log.error("Error while deleting review and rating: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while deleting review and rating", ex);
            throw new RuntimeException("Unexpected error while deleting review and rating", ex);
        }
    }

    // Method to calculate the average rating for a list of ratings and reviews
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

    // Method to get all products along with their calculated average ratings
    @Override
    public List<ProductDto> allProductsRating() {
        log.info("Getting all products with their associated ratings");

        List<ProductDto> products = productService.findProducts();
        return products.stream()
                .peek(productDto -> {
                    List<Ratingsreview> productReviews = ratingsreviewRepository.findAllByProductId(productDto.getId());
                    List<RatingsreviewDto> ratingsreviewDtos = productReviews.stream()
                            .map(ratingsreviewMapper::mapToDto)
                            .collect(Collectors.toList());
                    double rating = calculateProductRating(ratingsreviewDtos);
                    productDto.setRating(rating);
                })
                .collect(Collectors.toList());
    }
}