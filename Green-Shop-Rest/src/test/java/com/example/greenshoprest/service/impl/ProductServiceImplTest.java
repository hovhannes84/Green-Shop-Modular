package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.productDto.CreateProductRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.entity.Category;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.Ratingsreview;
import com.example.greenshopcommon.mapper.CategoryMapper;
import com.example.greenshopcommon.mapper.ProductMapper;
import com.example.greenshopcommon.repository.CategoryRepository;
import com.example.greenshopcommon.repository.ProductRepository;
import com.example.greenshopcommon.repository.RatingsreviewRepository;
import com.example.greenshoprest.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private RatingsreviewRepository ratingsreviewRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private final String uploadPath = "test-upload-path/";


    @Test
    public void testFindProducts() {

        Product product1 = new Product();
        product1.setId(1);
        product1.setName("Product1");

        Product product2 = new Product();
        product2.setId(2);
        product2.setName("Product2");

        List<Product> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);

        when(productRepository.findAll()).thenReturn(products);
        when(ratingsreviewRepository.findAllByProductId(1)).thenReturn(new ArrayList<>());
        when(ratingsreviewRepository.findAllByProductId(2)).thenReturn(new ArrayList<>());

        ProductDto productDto1 = new ProductDto();
        productDto1.setId(1);
        productDto1.setName("Product1");

        ProductDto productDto2 = new ProductDto();
        productDto2.setId(2);
        productDto2.setName("Product2");

        when(productMapper.mapToDto(product1)).thenReturn(productDto1);
        when(productMapper.mapToDto(product2)).thenReturn(productDto2);
        ResponseEntity<List<ProductDto>> responseEntity = productService.findProducts();
        List<ProductDto> result = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        assertEquals("Product1", result.get(0).getName());
        assertEquals("Product2", result.get(1).getName());

        verify(productRepository, times(1)).findAll();
        verify(ratingsreviewRepository, times(1)).findAllByProductId(1);
        verify(ratingsreviewRepository, times(1)).findAllByProductId(2);
        verify(productMapper, times(1)).mapToDto(product1);
        verify(productMapper, times(1)).mapToDto(product2);
        verify(categoryMapper, never()).mapToDto(any(Category.class));
    }

    @Test
    public void testFindById_WhenProductExists_ShouldReturnProductDtoWithRating() {
        int productId = 1;

        Product product = new Product();
        product.setId(productId);
        ProductDto productDto = new ProductDto();
        productDto.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(ratingsreviewRepository.findAllByProductId(productId)).thenReturn(new ArrayList<>());
        when(productMapper.mapToDto(product)).thenReturn(productDto);

        ResponseEntity<?> responseEntity = productService.findById(productId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(productDto, responseEntity.getBody());

        verify(productRepository, times(1)).findById(productId);
        verify(ratingsreviewRepository, times(1)).findAllByProductId(productId);
        verify(productMapper, times(1)).mapToDto(product);
        verify(categoryMapper, times(1)).mapToDto(product.getCategory());
    }

    @Test
    public void testFindById_WhenProductNotExists_ShouldReturnNotFound() {
        int productId = 1;
        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());
        ResponseEntity<?> responseEntity = productService.findById(productId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

        verify(productRepository, times(1)).findById(productId);
        verifyNoInteractions(ratingsreviewRepository);
        verifyNoInteractions(productMapper);
        verifyNoInteractions(categoryMapper);
    }


    @Test
    public void testAddProductWithNonExistingCategory() throws IOException {
        CreateProductRequestDto createProductRequestDto = new CreateProductRequestDto();
        createProductRequestDto.setId(1);
        createProductRequestDto.setName("Test Product");
        createProductRequestDto.setPrice(10.99);
        createProductRequestDto.setDescription("This is a test product.");
        createProductRequestDto.setQuantity(100);
        createProductRequestDto.setRating(4.5);

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1);
        createProductRequestDto.setCategoryDto(categoryDto);

        ProductRepository productRepository = mock(ProductRepository.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        RatingsreviewRepository ratingsreviewRepository = mock(RatingsreviewRepository.class);
        ProductMapper productMapper = mock(ProductMapper.class);
        CategoryMapper categoryMapper = mock(CategoryMapper.class);

        ProductService productService = new ProductServiceImpl(
                productRepository,
                productMapper,
                categoryMapper,
                categoryRepository,
                ratingsreviewRepository
        );
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());
        ResponseEntity<?> response = productService.addProduct(createProductRequestDto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUploadImage() throws IOException {
        int productId = 1;
        String originalFilename = "test_image.jpg";
        String contentType = "image/jpeg";
        byte[] content = {};
        MultipartFile multipartFile = new MockMultipartFile(
                originalFilename,
                originalFilename,
                contentType,
                content
        );
        ProductRepository productRepository = mock(ProductRepository.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        RatingsreviewRepository ratingsreviewRepository = mock(RatingsreviewRepository.class);
        ProductMapper productMapper = mock(ProductMapper.class);
        CategoryMapper categoryMapper = mock(CategoryMapper.class);

        ProductService productService = new ProductServiceImpl(
                productRepository,
                productMapper,
                categoryMapper,
                categoryRepository,
                ratingsreviewRepository
        );

        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        ResponseEntity<?> response = productService.uploadImage(productId, multipartFile);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUploadImageWithInvalidProduct() throws IOException {
        int productId = 1;
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                new byte[0]
        );

        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        ResponseEntity<ProductDto> response = productService.uploadImage(productId, multipartFile);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(productRepository, never()).save(any());
    }

    @Test
    public void testDeleteById_ProductExists_ReturnNoContent() {
        int productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.of(new Product()));
        ResponseEntity<?> response = productService.deleteById(productId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeleteById_ProductNotExists_ReturnNotFound() {
        int productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        ResponseEntity<?> response = productService.deleteById(productId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCalculateProductRating_EmptyRatings_ReturnZeroRating() {
        List<Ratingsreview> ratings = new ArrayList<>();
        double result = productService.calculateProductRating(ratings);
        assertEquals(0.0, result, 0.01);
    }

    @Test
    public void testCalculateProductRating_RatingsExists_ReturnAverageRating() {

        List<Ratingsreview> ratings = Arrays.asList(
                new Ratingsreview(),
                new Ratingsreview(),
                new Ratingsreview());
        ratings.get(0).setId(1);
        ratings.get(0).setRating(4.5);

        ratings.get(1).setId(2);
        ratings.get(1).setRating(3.0);

        ratings.get(2).setId(3);
        ratings.get(2).setRating(5.0);

        double result = productService.calculateProductRating(ratings);
        assertEquals(4.17, result, 0.01);
    }
}