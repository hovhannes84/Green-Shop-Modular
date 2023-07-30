package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.categoryDto.CreateCategoryRequestDto;
import com.example.greenshopcommon.dto.categoryDto.UpdateCategoryRequestDto;
import com.example.greenshopcommon.exception.EntityAlreadyExistsException;
import com.example.greenshopcommon.exception.EntityNotFoundException;
import com.example.greenshopcommon.exception.IllegalArgumentExceptionError;
import com.example.greenshoprest.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryEndpoint {

    private final CategoryService categoryService;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateCategoryRequestDto requestDto) throws IOException, IllegalArgumentExceptionError, EntityAlreadyExistsException {
        log.info("Creating a new category");
        return categoryService.addCategory(requestDto);
    }

    @GetMapping()
    public ResponseEntity<List<CategoryDto>> getAll() {
        log.info("Fetching all categories");
        return ResponseEntity.ok(categoryService.findCategories().getBody());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") int id) {
        log.info("Fetching category with ID: {}", id);
        return categoryService.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") int id, @RequestBody UpdateCategoryRequestDto updateCategoryRequestDto) {
        log.info("Updating category with ID: {}", id);
        return categoryService.update(id, updateCategoryRequestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") int id) throws EntityNotFoundException {
        log.info("Deleting category with ID: {}", id);
        return categoryService.deleteById(id);

    }

}
