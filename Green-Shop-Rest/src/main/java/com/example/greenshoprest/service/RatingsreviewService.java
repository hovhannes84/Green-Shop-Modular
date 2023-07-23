package com.example.greenshoprest.service;


import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.CreateRatingsreviewRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.RatingsreviewDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.UpdateRatingsreviewRequestDto;
import com.example.greenshoprest.security.CurrentUser;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RatingsreviewService {
    ResponseEntity<List<RatingsreviewDto>>getAllByProductId(int productId);
    ResponseEntity<List<RatingsreviewDto>> getAll();

    ResponseEntity<?> createReviewAndRating(CreateRatingsreviewRequestDto createRatingsreviewRequestDto, CurrentUser currentUser);

    ResponseEntity<?> getRatingsreviewByUserId(CurrentUser currentUser);

    ResponseEntity<?> updateRatingsreview(UpdateRatingsreviewRequestDto ratingsreview, CurrentUser currentUser);

    ResponseEntity<?> deleteRatingsreview(int id);
    double calculateProductRating(List<RatingsreviewDto> ratingsreviews);
    ResponseEntity<List<ProductDto>>  allProductsRating();
}