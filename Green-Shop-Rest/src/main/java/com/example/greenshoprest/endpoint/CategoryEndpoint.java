package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.categoryDto.CreateCategoryRequestDto;
import com.example.greenshopcommon.dto.categoryDto.UpdateCategoryRequestDto;
import com.example.greenshoprest.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryEndpoint {

    private final CategoryService categoryService;


    @PostMapping()
    public ResponseEntity<CategoryDto> create(@RequestBody CreateCategoryRequestDto requestDto) throws IOException {
        return ResponseEntity.ok(categoryService.addCategory(requestDto).getBody());
    }

    @GetMapping()
    public ResponseEntity<List<CategoryDto>> getAll() {
        return ResponseEntity.ok(categoryService.findCategories().getBody());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable("id") int id) {
        return ResponseEntity.ok(categoryService.findById(id).getBody());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable("id") int id, @RequestBody UpdateCategoryRequestDto updateCategoryRequestDto) {
        return ResponseEntity.ok(categoryService.update(id, updateCategoryRequestDto).getBody());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") int id) {
        return categoryService.deleteById(id);

    }

}
