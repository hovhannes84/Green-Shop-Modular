package com.example.greenshoprest.service;

import com.example.greenshopcommon.dto.orderDto.OrderDto;
import com.example.greenshopcommon.entity.User;

import java.util.List;

public interface OrderService {

    OrderDto getOrderById(int id);

    List<OrderDto> getOrdersByUserId(User currentUser);

    //    OrderDto addOrder(CreateOrderRequestDto createOrderRequestDto, User user);
    void deleteOrderById(int id);

}
