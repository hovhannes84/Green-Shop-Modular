package com.example.greenshoprest.service;

import com.example.greenshopcommon.dto.orderDto.CreateOrderRequestDto;
import com.example.greenshopcommon.dto.orderDto.OrderDto;
import com.example.greenshopcommon.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {

    ResponseEntity<OrderDto> getOrderById(int id);

    ResponseEntity<List<OrderDto>> getOrdersByUserId(User currentUser);

    ResponseEntity<OrderDto> addOrder(CreateOrderRequestDto createOrderRequestDto, User user);
    ResponseEntity<?> deleteOrderById(int id);

}

