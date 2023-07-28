package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.productDto.CreateProductRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.productDto.UpdateProductRequestDto;
import com.example.greenshoprest.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
public class ProductEndpoint {

    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<List<ProductDto>> getAll() {
        log.info("Fetching all products");
        return productService.findProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") int id) {
        log.info("Fetching product with ID: {}", id);
        return productService.findById(id);
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateProductRequestDto requestDto) throws IOException {
        log.info("Creating a new product");
        return productService.addProduct(requestDto);
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ProductDto> uploadImage(
            @PathVariable("id") int productId,
            @RequestParam("image") MultipartFile multipartFile) throws IOException {
        log.info("Uploading image for product with ID: {}", productId);
        return productService.uploadImage(productId, multipartFile);
    }

    @GetMapping(value = "/getImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@RequestParam("picName") String picName) throws IOException {
        log.info("Fetching image for product with name: {}", picName);
        byte[] imageData = productService.getImage(picName);
        if (imageData != null) {
            return ResponseEntity.ok(imageData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody UpdateProductRequestDto updateProductRequestDto) throws IOException {
        log.info("Updating product with ID: {}", id);
        return productService.updateProduct(id, updateProductRequestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") int id) {
        log.info("Deleting product with ID: {}", id);
        return productService.deleteById(id);
    }

}
