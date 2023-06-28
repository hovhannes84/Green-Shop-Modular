package com.example.greenshopweb.service;



import com.example.greenshopcommon.dto.orderDto.CreateOrderRequestDto;
import com.example.greenshopcommon.dto.orderDto.OrderDto;
import com.example.greenshopcommon.dto.orderDto.UpdateOrderRequestDto;
import com.example.greenshopcommon.entity.Order;

import java.util.List;

public interface OrderService {
    List<OrderDto> findOrdersByUser(int userId);
    Order findById(int id);
    void addOrders(CreateOrderRequestDto createOrderRequestDto, int userId);
    void deleteById(int id);
    void update(int id, UpdateOrderRequestDto order);
    OrderDto getOrderById(int id);
}
