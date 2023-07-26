package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.categoryDto.CreateCategoryRequestDto;
import com.example.greenshopcommon.dto.categoryDto.UpdateCategoryRequestDto;
import com.example.greenshopcommon.entity.Category;
import com.example.greenshopcommon.mapper.CategoryMapper;
import com.example.greenshopcommon.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void testAddCategorySuccess() throws IOException {
        String categoryName = "Test Category";
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(categoryName);
        Category categoryToAdd = new Category();
        categoryToAdd.setId(1);
        categoryToAdd.setName(categoryName);
        CategoryDto categoryDto = new CategoryDto(1, categoryName);
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());
        when(categoryMapper.map(requestDto)).thenReturn(categoryToAdd);
        when(categoryMapper.mapToDto(categoryToAdd)).thenReturn(categoryDto);

        ResponseEntity<CategoryDto> responseEntity = categoryService.addCategory(requestDto);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(categoryDto, responseEntity.getBody());
    }

    @Test
    void testAddCategoryConflict() throws IOException {
        String categoryName = "Test Category";
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(categoryName);
        Category existingCategory = new Category();
        existingCategory.setId(1);
        existingCategory.setName(categoryName);
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(existingCategory));
        ResponseEntity<CategoryDto> responseEntity = categoryService.addCategory(requestDto);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testFindCategoriesEmpty() {
        List<Category> emptyCategoryList = new ArrayList<>();
        when(categoryRepository.findAll()).thenReturn(emptyCategoryList);
        ResponseEntity<List<CategoryDto>> responseEntity = categoryService.findCategories();
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testFindCategories() {
        List<Category> categories = new ArrayList<>();
        Category category1 = new Category();
        category1.setId(1);
        category1.setName("Category 1");
        categories.add(category1);

        Category category2 = new Category();
        category2.setId(2);
        category2.setName("Category 2");
        categories.add(category2);

        List<CategoryDto> categoryDtos = new ArrayList<>();
        categoryDtos.add(new CategoryDto(1, "Category 1"));
        categoryDtos.add(new CategoryDto(2, "Category 2"));
        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.mapToDto(any(Category.class)))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    return categoryDtos.stream()
                            .filter(dto -> dto.getId() == category.getId())
                            .findFirst()
                            .orElse(null);
                });
        ResponseEntity<List<CategoryDto>> responseEntity = categoryService.findCategories();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        List<CategoryDto> returnedCategoryDtos = responseEntity.getBody();
        assertEquals(categoryDtos, returnedCategoryDtos);
    }

    @Test
    void testFindCategoryByIdFound() {
        int categoryId = 1;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Category 1");
        CategoryDto categoryDto = new CategoryDto(categoryId, "Category 1");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.mapToDto(category)).thenReturn(categoryDto);

        ResponseEntity<CategoryDto> responseEntity = categoryService.findById(categoryId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(categoryDto, responseEntity.getBody());
    }

    @Test
    void testFindCategoryByIdNotFound() {
        int categoryId = 1;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        ResponseEntity<CategoryDto> responseEntity = categoryService.findById(categoryId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testDeleteCategoryByIdExists() {
        int categoryId = 1;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        ResponseEntity<?> responseEntity = categoryService.deleteById(categoryId);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    void testDeleteCategoryByIdNotExists() {
        int categoryId = 1;
        when(categoryRepository.existsById(categoryId)).thenReturn(false);
        ResponseEntity<?> responseEntity = categoryService.deleteById(categoryId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(categoryRepository, never()).deleteById(categoryId);
    }

    @Test
    void testUpdateCategoryFound() {
        int categoryId = 1;
        String updatedCategoryName = "Updated Category";
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Category 1");
        UpdateCategoryRequestDto updateCategoryRequestDto = new UpdateCategoryRequestDto();
        updateCategoryRequestDto.setName(updatedCategoryName);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.mapToDto(category)).thenReturn(new CategoryDto(categoryId, updatedCategoryName));
        ResponseEntity<CategoryDto> responseEntity = categoryService.update(categoryId, updateCategoryRequestDto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(categoryId, responseEntity.getBody().getId());
        assertEquals(updatedCategoryName, responseEntity.getBody().getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testUpdateCategoryNotFound() {
        int categoryId = 1;
        UpdateCategoryRequestDto updateCategoryRequestDto = new UpdateCategoryRequestDto();
        updateCategoryRequestDto.setName("Updated Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        ResponseEntity<CategoryDto> responseEntity = categoryService.update(categoryId, updateCategoryRequestDto);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(categoryRepository, never()).save(any());
    }
}