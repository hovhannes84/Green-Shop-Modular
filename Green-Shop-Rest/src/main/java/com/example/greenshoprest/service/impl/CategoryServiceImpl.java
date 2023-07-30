package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.categoryDto.CreateCategoryRequestDto;
import com.example.greenshopcommon.dto.categoryDto.UpdateCategoryRequestDto;
import com.example.greenshopcommon.entity.Category;
import com.example.greenshopcommon.exception.EntityAlreadyExistsException;
import com.example.greenshopcommon.exception.EntityNotFoundException;
import com.example.greenshopcommon.mapper.CategoryMapper;
import com.example.greenshopcommon.repository.CategoryRepository;
import com.example.greenshoprest.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

//    Adds a new category to the database if a category with the same name does not exist.
    @Override
    public ResponseEntity<CategoryDto> addCategory(CreateCategoryRequestDto requestDto) throws EntityAlreadyExistsException {
        log.info("Adding a new category");
        // Validate requestDto
        if (requestDto == null || requestDto.getName() == null || requestDto.getName().isEmpty()) {
            throw new IllegalArgumentException("Category name must not be null or empty.");
        }
        // Check if a category with the same name already exists
        Optional<Category> byName = categoryRepository.findByName(requestDto.getName());
        if (byName.isPresent()) {
            log.info("Category with the given name already exists");
            throw new EntityAlreadyExistsException("Category with name '" + requestDto.getName() + "' already exists.");
        }
        // Create and save the new category
        Category category = categoryMapper.map(requestDto);
        categoryRepository.save(category);
        log.info("New category added successfully");
        return ResponseEntity.ok(categoryMapper.mapToDto(category));
    }
    // Fetches all categories from the database.
    @Override
    public ResponseEntity<List<CategoryDto>> findCategories() {
        log.info("Fetching all categories");
        List<Category> all = categoryRepository.findAll();
        if (all.isEmpty()) {
            log.info("No categories found");
            return ResponseEntity.notFound().build();
        } else {
            List<CategoryDto> categoryDtos = all.stream()
                    .map(categoryMapper::mapToDto)
                    .collect(Collectors.toList());
            log.info("Fetched {} categories", categoryDtos.size());
            return ResponseEntity.ok(categoryDtos);
        }
    }
    // Fetches a category by its ID from the database.
    @Override
    public ResponseEntity<CategoryDto> findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("The id cannot be 0 or less than 0: " + id);
        }
        log.info("Fetching category with ID: {}", id);
        return categoryRepository.findById(id)
                .map(categoryMapper::mapToDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    log.info("Category with ID {} not found", id);
                    throw new EntityNotFoundException("Category with ID " + id + " does not exist.");
                });
    }
    // Deletes a category by its ID from the database.
    @Override
    public ResponseEntity<?> deleteById(int id) throws EntityNotFoundException {
        if (id <= 0) {
            throw new IllegalArgumentException("The id cannot be 0 or less than 0: " + id);
        }
        log.info("Deleting category with ID: {}", id);
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            log.info("Category with ID {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        } else {
            log.info("Category with ID {} not found for deletion", id);
            throw new EntityNotFoundException("Category with ID " + id + " does not exist.");
        }
    }
    // Updates a category by its ID with the provided data in the requestDto.
    @Override
    public ResponseEntity<CategoryDto> update(int id, UpdateCategoryRequestDto updateCategoryRequestDto) {
        if (id <= 0) {
            throw new IllegalArgumentException("The id cannot be 0 or less than 0: " + id);
        }
        log.info("Updating category with ID: {}", id);
        if (updateCategoryRequestDto == null) {
            throw new IllegalArgumentException("updateCategoryRequestDto must not be null.");
        }
        return categoryRepository.findById(id)
                .map(category -> {
                    if (category.getName() != null && !category.getName().isEmpty()) {
                        category.setName(updateCategoryRequestDto.getName());
                    }
                    categoryRepository.save(category);
                    log.info("Category with ID {} updated successfully", id);
                    return ResponseEntity.ok(categoryMapper.mapToDto(category));
                })
                .orElseGet(() -> {
                    log.info("Category with ID {} not found for update", id);
                    throw new EntityNotFoundException("Cart with ID " + id + " does not exist.");
                });
    }
}
