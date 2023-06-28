package com.example.greenshopweb.service.impl;


import com.example.greenshopcommon.dto.orderDto.CreateOrderRequestDto;
import com.example.greenshopcommon.dto.orderDto.OrderDto;
import com.example.greenshopcommon.dto.orderDto.UpdateOrderRequestDto;
import com.example.greenshopcommon.entity.Cart;
import com.example.greenshopcommon.entity.Order;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.mapper.OrderMapper;
import com.example.greenshopcommon.mapper.ProductMapper;
import com.example.greenshopcommon.mapper.UserMapper;
import com.example.greenshopcommon.repository.CartRepository;
import com.example.greenshopcommon.repository.OrderRepository;
import com.example.greenshopcommon.repository.ProductRepository;
import com.example.greenshopcommon.repository.UserRepository;
import com.example.greenshopweb.service.CartService;
import com.example.greenshopweb.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;


    @Override
    public List<OrderDto> findOrdersByUser(int userId) {
        Optional<User> byId = userRepository.findById(userId);
        if (byId.isPresent()) {
            List<Order> orders = orderRepository.findAllByUserId(byId.get().getId());
            List<OrderDto> orderDtos = new ArrayList<>();
            for (Order order : orders) {
                OrderDto orderDto = orderMapper.mapToDto(order);
                orderDto.setUserDto(userMapper.mapToDto(order.getUser()));
                orderDto.setProduct(productMapper.mapToDto(order.getProduct()));
                orderDtos.add(orderDto);
            }
            return orderDtos;
        }
        return null;
    }

    @Override
    public Order findById(int id) {
        Optional<Order> byId = orderRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    @Override
    public void addOrders(CreateOrderRequestDto createOrderRequestDto, int userId) {
        Order order =orderMapper.map(createOrderRequestDto);
        Optional<User> byId = userRepository.findById(userId);
        if (byId.isPresent()) {
            order.setUser(byId.get());
            Optional<Product> byId1 = productRepository.findById(createOrderRequestDto.getProductDto().getId());
            if (byId1.isPresent()){
                order.setProduct(byId1.get());
            }
            orderRepository.save(order);
        }
        Optional<Cart> byProductId = cartRepository.findByProductId(order.getProduct().getId());
        if (byProductId.isPresent()) {
            Cart cart = byProductId.get();
            Optional<Product> byIdProd = productRepository.findById(cart.getProduct().getId());
            if (byIdProd.isPresent()) {
                Product product = byIdProd.get();
                product.setQuantity(product.getQuantity() - cart.getQuantity());
                productRepository.save(product);
            }
            cartService.deleteById(cart.getId());
        }
    }

    @Override
    public void deleteById(int id) {
        orderRepository.deleteById(id);
    }

    @Override
    public void update(int id, UpdateOrderRequestDto updateOrderRequestDto) {
        Order order =orderMapper.updateDto(updateOrderRequestDto);
        Optional<Product> byId1 = productRepository.findById(updateOrderRequestDto.getProductDto().getId());
        if (byId1.isPresent()){
            order.setProduct(byId1.get());
        }
        Optional<User> byUser = userRepository.findById(updateOrderRequestDto.getUserDto().getId());
        if (byUser.isPresent()) {
            order.setUser(byUser.get());}
        Optional<Order> byId = orderRepository.findById(id);
        if (byId.isPresent()) {
            Order orderDb = byId.get();
            orderDb.setUser(order.getUser());
            orderDb.setProduct(order.getProduct());
            orderDb.setOrderDate(order.getOrderDate());
            orderRepository.save(orderDb);
        }

    }
    public OrderDto getOrderById(int id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.map(orderMapper::mapToDto).orElse(null);
    }
}
