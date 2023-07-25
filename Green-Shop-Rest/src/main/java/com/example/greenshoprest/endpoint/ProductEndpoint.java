package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.productDto.CreateProductRequestDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.productDto.UpdateProductRequestDto;
import com.example.greenshoprest.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductEndpoint {

    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<List<ProductDto>> getAll() {
        return productService.findProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") int id) {
        return  productService.findById(id);
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateProductRequestDto requestDto) throws IOException {
        return productService.addProduct(requestDto);
    }
    @PostMapping("/{id}/image")
    public ResponseEntity<ProductDto> uploadImage(
            @PathVariable("id") int productId,
            @RequestParam("image") MultipartFile multipartFile) throws IOException {
        return productService.uploadImage(productId,multipartFile);
    }
    @GetMapping(value = "/getImage",
            produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getImage(@RequestParam("picName") String picName) throws IOException {
        return productService.getImage(picName);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody UpdateProductRequestDto updateProductRequestDto) throws IOException {
        return productService.updateProduct(id, updateProductRequestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") int id) {
        return productService.deleteById(id);

    }

}
