package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.categoryDto.CreateCategoryRequestDto;
import com.example.greenshopcommon.dto.categoryDto.UpdateCategoryRequestDto;
import com.example.greenshopcommon.entity.Category;
import com.example.greenshopcommon.mapper.CategoryMapper;
import com.example.greenshopcommon.repository.CategoryRepository;
import com.example.greenshoprest.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    public ResponseEntity<CategoryDto> addCategory(CreateCategoryRequestDto requestDto) throws IOException {
        Optional<Category> byName = categoryRepository.findByName(requestDto.getName());
        if (byName.isEmpty()) {
            Category category = categoryMapper.map(requestDto);
            categoryRepository.save(category);
            return ResponseEntity.ok(categoryMapper.mapToDto(category));
        }
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .build();
    }

    @Override
    public ResponseEntity<List<CategoryDto>> findCategories() {
        List<Category> all = categoryRepository.findAll();
        return all.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(all.stream()
                .map(categoryMapper::mapToDto)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<CategoryDto> findById(int id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::mapToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<?> deleteById(int id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<CategoryDto> update(int id, UpdateCategoryRequestDto updateCategoryRequestDto) {
        return categoryRepository.findById(id)
                .map(category -> {
                    if (category.getName() != null && !category.getName().isEmpty()) {
                        category.setName(updateCategoryRequestDto.getName());
                    }
                    categoryRepository.save(category);
                    return ResponseEntity.ok(categoryMapper.mapToDto(category));
                })
                .orElse(ResponseEntity.notFound().build());
    }

}
