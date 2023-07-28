package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.CreateRatingsreviewRequestDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.RatingsreviewDto;
import com.example.greenshopcommon.dto.ratingsreviewDto.UpdateRatingsreviewRequestDto;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.Ratingsreview;
import com.example.greenshopcommon.entity.Role;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.mapper.RatingsreviewMapper;
import com.example.greenshopcommon.repository.ProductRepository;
import com.example.greenshopcommon.repository.RatingsreviewRepository;
import com.example.greenshoprest.security.CurrentUser;
import com.example.greenshoprest.service.ProductService;
import com.example.greenshoprest.service.RatingsreviewService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RatingsreviewServiceImplTest {

    @Mock
    private RatingsreviewRepository ratingsreviewRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RatingsreviewMapper ratingsreviewMapper;

    @InjectMocks
    private RatingsreviewServiceImpl ratingsreviewService;


    @Test
    void testGetAllByProductId() {

        int productId = 1;
        List<Ratingsreview> ratingsreviews = Collections.singletonList(new Ratingsreview());
        List<RatingsreviewDto> expectedDtos = Collections.singletonList(new RatingsreviewDto());
        when(ratingsreviewRepository.findAllByProductId(productId)).thenReturn(ratingsreviews);
        when(ratingsreviewMapper.mapToDto(any(Ratingsreview.class))).thenReturn(new RatingsreviewDto());

        // Call the method and verify
        ResponseEntity<List<RatingsreviewDto>> response = ratingsreviewService.getAllByProductId(productId);
        assertEquals(expectedDtos, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testCreateReviewAndRating() {

        int productId = 1;
        CreateRatingsreviewRequestDto requestDto = new CreateRatingsreviewRequestDto();
        ProductDto productDto = new ProductDto();
        productDto.setId(productId);
        productDto.setName("Test Product");
        requestDto.setProductDto(productDto);

        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setEnabled(true);
        user.setRole(Role.CUSTOMER);
        CurrentUser currentUser = new CurrentUser(user);

        Ratingsreview ratingsreview = new Ratingsreview();
        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));
        when(ratingsreviewMapper.map(any(CreateRatingsreviewRequestDto.class))).thenReturn(ratingsreview);
        when(ratingsreviewRepository.save(any(Ratingsreview.class))).thenReturn(ratingsreview);
        when(ratingsreviewMapper.mapToDto(any(Ratingsreview.class))).thenReturn(new RatingsreviewDto());

        // Call the method and verify
        ResponseEntity<?> response = ratingsreviewService.createReviewAndRating(requestDto, currentUser);
        assertEquals(200, response.getStatusCodeValue());

        // Verify interactions with the repositories and mapper
        verify(productRepository, times(1)).findById(productId);
        verify(ratingsreviewRepository, times(1)).save(any(Ratingsreview.class));
        verify(ratingsreviewMapper, times(1)).map(any(CreateRatingsreviewRequestDto.class));
        verify(ratingsreviewMapper, times(1)).mapToDto(any(Ratingsreview.class));
    }

    @Test
    void testGetAll() {

        Ratingsreview ratingsreview1 = new Ratingsreview();
        Ratingsreview ratingsreview2 = new Ratingsreview();
        ratingsreview1.setId(1);
        ratingsreview1.setReview("Review 1");
        ratingsreview2.setId(2);
        ratingsreview2.setReview("Review 2");

        RatingsreviewDto dto1 = new RatingsreviewDto();
        RatingsreviewDto dto2 = new RatingsreviewDto();
        dto1.setId(1);
        dto1.setReview("Review 1");
        dto2.setId(2);
        dto2.setReview("Review 2");

        List<Ratingsreview> ratingsreviews = new ArrayList<>();
        ratingsreviews.add(ratingsreview1);
        ratingsreviews.add(ratingsreview2);

        List<RatingsreviewDto> expectedDtoList = new ArrayList<>();
        expectedDtoList.add(dto1);
        expectedDtoList.add(dto2);

        // Mock the repository behavior
        when(ratingsreviewRepository.findAll()).thenReturn(ratingsreviews);

        // Mock the mapper behavior
        when(ratingsreviewMapper.mapToDto(ratingsreview1)).thenReturn(dto1);
        when(ratingsreviewMapper.mapToDto(ratingsreview2)).thenReturn(dto2);

        // Call the method and verify
        ResponseEntity<List<RatingsreviewDto>> response = ratingsreviewService.getAll();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedDtoList, response.getBody());

        // Verify interactions with the repository and mapper
        verify(ratingsreviewRepository, times(1)).findAll();
        verify(ratingsreviewMapper, times(1)).mapToDto(ratingsreview1);
        verify(ratingsreviewMapper, times(1)).mapToDto(ratingsreview2);
    }

    @Test
    void testGetRatingsreviewByUserId_NoCurrentUser() {
        // Mock data
        CurrentUser currentUser = null;

        // Call the method and verify
        ResponseEntity<?> response = ratingsreviewService.getRatingsreviewByUserId(currentUser);
        assertEquals(204, response.getStatusCodeValue());
        assertEquals(ResponseEntity.noContent().build(), response);

        // Verify that the repository and mapper methods were not called
        verify(ratingsreviewRepository, never()).findRatingsreviewByUserId(anyInt());
        verify(ratingsreviewMapper, never()).mapToDto(any(Ratingsreview.class));
    }

    @Test
    @WithMockUser("poxos")
    void testUpdateRatingsreview() {
        int productId = 1;
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setEnabled(true);
        user.setRole(Role.CUSTOMER);
        CurrentUser currentUser = new CurrentUser(user);

        ProductDto productDto = new ProductDto();
        productDto.setId(productId);
        productDto.setName("Test Product");

        UpdateRatingsreviewRequestDto requestDto = new UpdateRatingsreviewRequestDto();
        requestDto.setProductDto(productDto);

        Ratingsreview ratingsreview = new Ratingsreview();
        ratingsreview.setId(1);
        ratingsreview.setReview("Review 1");

        // Mock the productRepository behavior
        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));

        // Mock the ratingsreviewMapper behavior
        when(ratingsreviewMapper.updateDto(requestDto)).thenReturn(ratingsreview);

        // Call the method and verify
        ResponseEntity<?> response = ratingsreviewService.updateRatingsreview(requestDto, currentUser);
        assertEquals(200, response.getStatusCodeValue());

        // Verify interactions with the productRepository, ratingsreviewRepository, and ratingsreviewMapper
        verify(productRepository, times(1)).findById(productId);
        verify(ratingsreviewRepository, times(1)).save(ratingsreview);
        verify(ratingsreviewMapper, times(1)).updateDto(requestDto);
        verify(ratingsreviewMapper, times(1)).mapToDto(ratingsreview);
    }

    @Test
    void testDeleteRatingsreview() {

        int ratingsreviewId = 1;

        // Mock the repository behavior
        Ratingsreview mockRatingsreview = new Ratingsreview();
        mockRatingsreview.setId(ratingsreviewId);
        when(ratingsreviewRepository.findById(ratingsreviewId)).thenReturn(Optional.of(mockRatingsreview));

        // Call the method and verify
        ResponseEntity<?> response = ratingsreviewService.deleteRatingsreview(ratingsreviewId);
        assertEquals(204, response.getStatusCodeValue());

        // Verify interactions with the repository
        verify(ratingsreviewRepository, times(1)).findById(ratingsreviewId);
        verify(ratingsreviewRepository, times(1)).deleteById(ratingsreviewId);
    }

    @Test
    void testDeleteRatingsreviewNotFound() {
        // Mock data
        int ratingsreviewId = 1;

        // Mock the repository behavior to return an empty Optional (entity not found)
        when(ratingsreviewRepository.findById(ratingsreviewId)).thenReturn(Optional.empty());

        // Call the method and verify
        ResponseEntity<?> response = ratingsreviewService.deleteRatingsreview(ratingsreviewId);
        assertEquals(404, response.getStatusCodeValue());

        // Verify interactions with the repository
        verify(ratingsreviewRepository, times(1)).findById(ratingsreviewId);
        verify(ratingsreviewRepository, never()).deleteById(ratingsreviewId);
    }

    @Test
    void testAllProductsRating() {
        // Create a mock for ProductService
        ProductService productService = mock(ProductService.class);

        // Create a list of ProductDto representing products
        List<ProductDto> products = new ArrayList<>();

        ProductDto productDto1 = new ProductDto();
        productDto1.setId(1);
        productDto1.setName("Product 1");
        productDto1.setRating(4.5);

        ProductDto productDto2 = new ProductDto();
        productDto2.setId(2);
        productDto2.setName("Product 2");
        productDto2.setRating(3.8);

        // Add some products to the list (you can add more products as needed)
        products.add(productDto1);
        products.add(productDto2);

        // Set up the mock behavior for findProducts()
        when(productService.findProducts()).thenReturn(ResponseEntity.ok(products));

        // Create a RatingsreviewService with the mocked ProductService
        RatingsreviewService ratingsreviewService = new RatingsreviewServiceImpl(ratingsreviewRepository, productService, productRepository, ratingsreviewMapper);

        // Call the method and verify
        ResponseEntity<List<ProductDto>> response = ratingsreviewService.allProductsRating();
        assertEquals(200, response.getStatusCodeValue());

        // Verify interactions with the productService
        verify(productService, times(1)).findProducts();
    }

    @Test
    void testCalculateProductRating() {
        // Create some RatingsreviewDto objects representing ratings
        RatingsreviewDto rating1 = new RatingsreviewDto();
        rating1.setRating(4.5);

        RatingsreviewDto rating2 = new RatingsreviewDto();
        rating2.setRating(3.8);

        RatingsreviewDto rating3 = new RatingsreviewDto();
        rating3.setRating(5.0);

        // Add the ratings to a list
        List<RatingsreviewDto> ratingsreviews = new ArrayList<>();
        ratingsreviews.add(rating1);
        ratingsreviews.add(rating2);
        ratingsreviews.add(rating3);

        RatingsreviewService ratingsreviewService = new RatingsreviewServiceImpl(ratingsreviewRepository, productService, productRepository, ratingsreviewMapper);
        double averageRating = ratingsreviewService.calculateProductRating(ratingsreviews);
        assertEquals(4.43, averageRating, 0.01);
    }
}