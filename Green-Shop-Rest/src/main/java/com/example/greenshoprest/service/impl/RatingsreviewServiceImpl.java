package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.CreateRatingsreviewRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.RatingsreviewDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.UpdateRatingsreviewRequestDto;
import com.example.greenshopcommon.entity.Ratingsreview;
import com.example.greenshopcommon.mapper.RatingsreviewMapper;
import com.example.greenshopcommon.repository.ProductRepository;
import com.example.greenshopcommon.repository.RatingsreviewRepository;
import com.example.greenshoprest.security.CurrentUser;
import com.example.greenshoprest.service.ProductService;
import com.example.greenshoprest.service.RatingsreviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingsreviewServiceImpl implements RatingsreviewService {

    private final RatingsreviewRepository ratingsreviewRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final RatingsreviewMapper ratingsreviewMapper;

    @Override
    public ResponseEntity<List<RatingsreviewDto>> getAllByProductId(int productId) {
        List<RatingsreviewDto> ratingDtoAll = ratingsreviewRepository
                .findAllByProductId(productId)
                .stream()
                .map(ratingsreviewMapper::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ratingDtoAll);
    }

    @Override
    public ResponseEntity<List<RatingsreviewDto>> getAll() {
        List<RatingsreviewDto> ratingDtoAll = ratingsreviewRepository
                .findAll()
                .stream()
                .map(ratingsreviewMapper::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ratingDtoAll);
    }
    @Override
    public ResponseEntity<?> createReviewAndRating(CreateRatingsreviewRequestDto createRatingsreviewRequestDto, CurrentUser currentUser) {
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
    @Override
    public ResponseEntity<?> getRatingsreviewByUserId(CurrentUser currentUser) {
        return Optional.ofNullable(currentUser)
                .map(CurrentUser::getUser)
                .flatMap(user -> ratingsreviewRepository.findRatingsreviewByUserId(user.getId())
                        .map(ratingsreviewMapper::mapToDto))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    @Override
    public ResponseEntity<?> updateRatingsreview(UpdateRatingsreviewRequestDto updateRatingsreviewRequestDto, CurrentUser currentUser) {
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
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
    @Override
    public ResponseEntity<?> deleteRatingsreview(int id) {
        return ratingsreviewRepository.findById(id)
                .map(ratingsreview -> {
                    ratingsreviewRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<ProductDto>> allProductsRating() {
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