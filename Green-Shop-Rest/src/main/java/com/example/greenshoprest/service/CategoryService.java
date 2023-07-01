package com.example.greenshoprest.service;

import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.categoryDto.CreateCategoryRequestDto;
import com.example.greenshopcommon.dto.categoryDto.UpdateCategoryRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

public interface CategoryService {

    ResponseEntity<CategoryDto> addCategory(CreateCategoryRequestDto author) throws IOException;

    ResponseEntity<List<CategoryDto>> findCategories();

    ResponseEntity<CategoryDto> findById(int id);

    ResponseEntity<?> deleteById(int id);

    public ResponseEntity<CategoryDto> update(@PathVariable("id") int id, @RequestBody UpdateCategoryRequestDto updateCategoryRequestDto);

}
