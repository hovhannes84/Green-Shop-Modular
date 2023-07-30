package com.example.greenshoprest.service;


import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.dto.cartDto.CreateCartRequestDto;
import com.example.greenshopcommon.dto.cartDto.UpdateCartRequestDto;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.exception.IllegalArgumentExceptionError;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CartService {
    ResponseEntity<List<CartDto>> findCartsByUser(User user);
    ResponseEntity<CartDto> findById(int id) throws EntityNotFoundException, com.example.greenshopcommon.exception.EntityNotFoundException;

    ResponseEntity<CartDto> addCart(User user,CreateCartRequestDto createCartRequestDto) throws IllegalArgumentExceptionError;

    ResponseEntity<?> deleteById(int id);

    public ResponseEntity<CartDto> updateCart(int id,User user, UpdateCartRequestDto updateCartRequestDto);
}
