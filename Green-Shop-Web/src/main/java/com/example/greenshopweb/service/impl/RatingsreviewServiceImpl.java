package com.example.greenshopweb.service.impl;


import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.CreateRatingsreviewRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.RatingsreviewDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.UpdateRatingsreviewRequestDto;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.Ratingsreview;
import com.example.greenshopcommon.mapper.ProductMapper;
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

@RequiredArgsConstructor
@Service
public class RatingsreviewServiceImpl implements RatingsreviewService {

    private final RatingsreviewRepository ratingsreviewRepository;
    private final ProductService productService;
    private final RatingsreviewMapper ratingsreviewMapper;
    private final ProductMapper productMapper;

    @Override
    public List<RatingsreviewDto> getAllByProductId(int productId) {
        List<Ratingsreview> all = ratingsreviewRepository.findAllByProductId(productId);
        List<RatingsreviewDto> ratingDtoAll = new ArrayList<>();
        for (Ratingsreview ratingsreview : all) {
            RatingsreviewDto ratingsreviewDto = ratingsreviewMapper.mapToDto(ratingsreview);
            ratingDtoAll.add(ratingsreviewDto);
        }
        return ratingDtoAll;

    }

    @Override
    public List<RatingsreviewDto> getAll() {
        List<Ratingsreview> all = ratingsreviewRepository.findAll();
        List<RatingsreviewDto> ratingDtoAll = new ArrayList<>();
        for (Ratingsreview ratingsreview : all) {
            RatingsreviewDto ratingsreviewDto = ratingsreviewMapper.mapToDto(ratingsreview);
            ratingDtoAll.add(ratingsreviewDto);
        }
        return ratingDtoAll;
    }

    @Override
    public void createReviewAndRating(CreateRatingsreviewRequestDto createRatingsreviewRequestDto, CurrentUser currentUser) {
        Optional<Product> productOptional = productService.findById(createRatingsreviewRequestDto.getProductDto().getId());
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Ratingsreview ratingsreview = ratingsreviewMapper.map(createRatingsreviewRequestDto);
            ratingsreview.setProduct(product);
            ratingsreview.setUser(currentUser.getUser());
            ratingsreviewRepository.save(ratingsreview);
        }
    }


    @Override
    public Ratingsreview getRatingsreviewByUserId(CurrentUser currentUser) {
        int userId = currentUser.getUser().getId();
        Optional<Ratingsreview> ratingsreview = ratingsreviewRepository.findRatingsreviewByUserId(userId);
        if (ratingsreview.isPresent()) {
            return ratingsreview.get();
        }
        return null;
    }

    @Override
    public void updateRatingsreview(UpdateRatingsreviewRequestDto updateRatingsreviewRequestDto, CurrentUser currentUser) {
        Optional<Product> productOptional = productService.findById(updateRatingsreviewRequestDto.getProductDto().getId());
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Ratingsreview ratingsreview = ratingsreviewMapper.updateDto(updateRatingsreviewRequestDto);
            ratingsreview.setProduct(product);
            ratingsreview.setUser(currentUser.getUser());
            ratingsreviewRepository.save(ratingsreview);
        }
    }

    @Override
    public void deleteRatingsreview(int id) {
        ratingsreviewRepository.findById(id).ifPresent(ratingsreviewRepository::delete);
    }

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

    @Override
    public List<ProductDto> allProductsRating() {
        List<ProductDto> products = productService.findProducts();
        List<ProductDto> productsWithRating = new ArrayList<>();
        for (ProductDto productDto : products) {
            List<Ratingsreview> productReviews = ratingsreviewRepository.findAllByProductId(productDto.getId());
            List<RatingsreviewDto> ratingsreviewDtos = new ArrayList<>();
            for (Ratingsreview ratingsreview : productReviews) {
                RatingsreviewDto ratingsreviewDto = ratingsreviewMapper.mapToDto(ratingsreview);
                ratingsreviewDtos.add(ratingsreviewDto);
            }
            double rating = calculateProductRating(ratingsreviewDtos);
            productDto.setRating(rating);
            productsWithRating.add(productDto);
        }
        return productsWithRating;
    }

}