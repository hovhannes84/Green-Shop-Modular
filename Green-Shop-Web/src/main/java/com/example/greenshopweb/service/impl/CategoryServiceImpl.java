package com.example.greenshopweb.service.impl;


import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.categoryDto.CreateCategoryRequestDto;
import com.example.greenshopcommon.entity.Category;
import com.example.greenshopcommon.mapper.CategoryMapper;
import com.example.greenshopcommon.repository.CategoryRepository;
import com.example.greenshopweb.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : categories) {
            CategoryDto categoryDto = categoryMapper.mapToDto(category);
            categoryDtos.add(categoryDto);
        }
        return categoryDtos;
    }

    @Override
    public Optional<Category> findById(int id) {
        return categoryRepository.findById(id);
    }

    @Override
    public void addCategory(CreateCategoryRequestDto createCategoryRequestDto) throws IOException {
        Category category = categoryMapper.map(createCategoryRequestDto);
        categoryRepository.save(category);
    }

    @Override
    public void deleteById(int id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public void updateCategory(Category category) {
        categoryRepository.save(category);
    }

    public CategoryDto singleCategoryPage(int id){
        Optional<Category> byId = categoryRepository.findById(id);
        if (byId.isPresent()) {
            return categoryMapper.mapToDto(byId.get());
        }return null;
    }

}
