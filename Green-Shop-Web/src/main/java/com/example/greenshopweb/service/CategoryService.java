package com.example.greenshopweb.service;



import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.categoryDto.CreateCategoryRequestDto;
import com.example.greenshopcommon.entity.Category;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<CategoryDto> findCategories();
    Optional<Category> findById(int id);

    void addCategory(CreateCategoryRequestDto createCategoryRequestDto) throws IOException;

    void deleteById(int id);
    public void updateCategory(Category category);


    CategoryDto singleCategoryPage(int id);

}
