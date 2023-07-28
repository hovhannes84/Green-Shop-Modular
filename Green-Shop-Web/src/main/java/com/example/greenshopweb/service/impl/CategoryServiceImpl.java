package com.example.greenshopweb.service.impl;

import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.categoryDto.CreateCategoryRequestDto;
import com.example.greenshopcommon.entity.Category;
import com.example.greenshopcommon.mapper.CategoryMapper;
import com.example.greenshopcommon.repository.CategoryRepository;
import com.example.greenshopweb.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    // Method to retrieve all categories and return a list of CategoryDto objects.
    @Override
    public List<CategoryDto> findCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : categories) {
            CategoryDto categoryDto = categoryMapper.mapToDto(category);
            categoryDtos.add(categoryDto);
        }
        log.info("Retrieved all categories: {}", categoryDtos);
        return categoryDtos;
    }

    // Method to find a category by its ID and return an Optional<Category> object.
    @Override
    public Optional<Category> findById(int id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            log.info("Category found by ID {}: {}", id, category.get());
        } else {
            log.warn("Category not found for ID: {}", id);
        }
        return category;
    }

    // Method to add a new category based on the provided CreateCategoryRequestDto.
    @Override
    public void addCategory(@Valid CreateCategoryRequestDto createCategoryRequestDto) throws IOException {
        try {
            if (createCategoryRequestDto.getName() == null || createCategoryRequestDto.getName().isEmpty()) {
                throw new IllegalArgumentException("Category name must not be empty");
            }
            Category category = categoryMapper.map(createCategoryRequestDto);
            categoryRepository.save(category);
            log.info("New category added: {}", category);
        } catch (Exception e) {
            log.error("Error occurred while adding a category: {}", e.getMessage());
            throw new IOException("Failed to add a new category", e);
        }
    }

    // Method to delete a category by its ID.
    @Override
    public void deleteById(int id) {
        categoryRepository.deleteById(id);
        log.info("Category deleted with ID: {}", id);
    }

    // Method to update an existing category.
    @Override
    public void updateCategory(@Valid Category category) {
        try {
            if (category.getName() == null || category.getName().isEmpty()) {
                throw new IllegalArgumentException("Category name must not be empty");
            }
            categoryRepository.save(category);
            log.info("Category updated: {}", category);
        } catch (Exception e) {
            log.error("Error occurred while updating a category: {}", e.getMessage());
            throw new RuntimeException("Failed to update the category", e);
        }
    }


    // Method to retrieve a single category page by its ID and return a CategoryDto object.    @Override
    public CategoryDto singleCategoryPage(int id) {
        try {
            Optional<Category> byId = categoryRepository.findById(id);
            if (byId.isPresent()) {
                log.info("Retrieved single category by ID {}: {}", id, byId.get());
                return categoryMapper.mapToDto(byId.get());
            } else {
                log.warn("Category not found for ID: {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Error occurred while retrieving a category: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve the category", e);
        }
    }
}
