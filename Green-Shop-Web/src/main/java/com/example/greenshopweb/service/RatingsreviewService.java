package com.example.greenshopweb.service;

import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.CreateRatingsreviewRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.RatingsreviewDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.UpdateRatingsreviewRequestDto;
import com.example.greenshopcommon.entity.Ratingsreview;
import com.example.greenshopweb.security.CurrentUser;

import java.util.List;

public interface RatingsreviewService {

    List<RatingsreviewDto> getAllByProductId(int productId);
    List<RatingsreviewDto> getAll();

    void createReviewAndRating(CreateRatingsreviewRequestDto createRatingsreviewRequestDto, CurrentUser currentUser);

    Ratingsreview getRatingsreviewByUserId(CurrentUser currentUser);

    void updateRatingsreview(UpdateRatingsreviewRequestDto ratingsreview, CurrentUser currentUser);

    void deleteRatingsreview(int id);
    public double calculateProductRating(List<RatingsreviewDto> ratingsreviews);

    public List<ProductDto> allProductsRating();
}
