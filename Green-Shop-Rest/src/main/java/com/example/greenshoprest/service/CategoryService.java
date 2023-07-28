package com.example.greenshoprest.service;

import com.example.greenshopcommon.dto.categoryDto.CategoryDto;
import com.example.greenshopcommon.dto.categoryDto.CreateCategoryRequestDto;
import com.example.greenshopcommon.dto.categoryDto.UpdateCategoryRequestDto;
import com.example.greenshopcommon.exception.EntityAlreadyExistsException;
import com.example.greenshopcommon.exception.EntityNotFoundException;
import com.example.greenshopcommon.exception.IllegalArgumentExceptionError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.List;

public interface CategoryService {

    ResponseEntity<CategoryDto> addCategory(CreateCategoryRequestDto author) throws IOException, IllegalArgumentExceptionError, EntityAlreadyExistsException;

    ResponseEntity<List<CategoryDto>> findCategories();

    ResponseEntity<CategoryDto> findById(int id);

    ResponseEntity<?> deleteById(int id) throws EntityNotFoundException;

    public ResponseEntity<CategoryDto> update(@PathVariable("id") int id, @RequestBody UpdateCategoryRequestDto updateCategoryRequestDto);

}
