package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.productDto.CreateProductRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshoprest.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
public class ProductEndpointTest {



    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;


    @Test
    public void testGetAll_ProductsExist_ReturnProductList() throws Exception {
        ProductDto product1 = new ProductDto();
        product1.setId(1);
        product1.setName("Product1");

        ProductDto product2 = new ProductDto();
        product2.setId(2);
        product2.setName("Product2");

        List<ProductDto> productList = Arrays.asList(product1, product2);
        when(productService.findProducts()).thenReturn(ResponseEntity.ok(productList));

        mockMvc.perform(MockMvcRequestBuilders.get("/product"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Product1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Product2"));
    }


    @Test
    public void testGetAll_NoProductsExist_ReturnEmptyList() throws Exception {
        List<ProductDto> emptyProductList = Collections.emptyList();
        when(productService.findProducts()).thenReturn(ResponseEntity.ok(emptyProductList));

        mockMvc.perform(MockMvcRequestBuilders.get("/product"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    public void testGetAll_ProductServiceReturnsError_ReturnErrorStatus() throws Exception {
        when(productService.findProducts()).thenReturn(ResponseEntity.status(500).build());
        mockMvc.perform(MockMvcRequestBuilders.get("/product"))
                .andExpect(status().isInternalServerError());
    }



    @Test
    public void testGetById_ProductNotExists_ReturnNotFound() throws Exception {
        int productId = 1;
        when(productService.findById(productId)).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(MockMvcRequestBuilders.get("/product/{id}", productId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreate_ProductAddedSuccessfully_ReturnsOk() throws Exception {
        CreateProductRequestDto requestDto = new CreateProductRequestDto();
        requestDto.setName("Test Product");
        requestDto.setPrice(10.99);
        requestDto.setDescription("This is a test product.");
        requestDto.setQuantity(100);
        requestDto.setRating(4.5);
        when(productService.addProduct(any(CreateProductRequestDto.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(MockMvcRequestBuilders.post("/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUploadImage_SuccessfullyUploadsImage_ReturnsOk() throws Exception {
        int productId = 1;
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test-image.jpg", MediaType.IMAGE_JPEG_VALUE, "Test Image".getBytes()
        );
        when(productService.uploadImage(anyInt(), any(MultipartFile.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/product/{id}/image", productId)
                        .file("image", imageFile.getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetImage_ValidImageName_ReturnsImageBytes() throws Exception {
        String imageName = "test-image.jpg";
        byte[] imageBytes = new byte[] {  };
        when(productService.getImage(imageName)).thenReturn(imageBytes);

        mockMvc.perform(MockMvcRequestBuilders.get("/product/getImage")
                        .param("picName", imageName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    public void testGetImage_InvalidImageName_ReturnsNotFound() throws Exception {
        String imageName = "nonexistent-image.jpg";
        when(productService.getImage(anyString())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get("/product/getImage")
                        .param("picName", imageName))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("poxos")
    public void testDeleteById_ProductExists_ReturnsOk() throws Exception {
        int productId = 1;
        when(productService.deleteById(productId)).thenReturn(ResponseEntity.ok().build());
        mockMvc.perform(MockMvcRequestBuilders.delete("/product/{id}", productId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("poxos")
    public void testDeleteById_ProductNotExists_ReturnsNotFound() throws Exception {
        int productId = 1;
        when(productService.deleteById(productId)).thenReturn(ResponseEntity.notFound().build());
        mockMvc.perform(MockMvcRequestBuilders.delete("/product/{id}", productId))
                .andExpect(status().isNotFound());
    }
}