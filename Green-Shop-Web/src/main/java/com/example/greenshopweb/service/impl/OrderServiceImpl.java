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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {


    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;


    // Method to find all orders of a specific user
    @Override
    public List<OrderDto> findOrdersByUser(int userId) {
        log.info("Finding orders for user with ID: {}", userId);
        Optional<User> byId = userRepository.findById(userId);
        if (byId.isPresent()) {
            List<Order> orders = orderRepository.findAllByUserId(byId.get().getId());
            List<OrderDto> orderDtos = new ArrayList<>();
            for (Order order : orders) {
                OrderDto orderDto = orderMapper.mapToDto(order);
                orderDto.setUserDto(userMapper.mapToDto(order.getUser()));
                orderDto.setProductDto(productMapper.mapToDto(order.getProduct()));
                orderDtos.add(orderDto);
            }
            return orderDtos;
        } else {
            log.error("User with ID {} not found.", userId);
            throw new IllegalArgumentException("User not found");
        }
    }


    // Method to find an order by its ID
    @Override
    public Order findById(int id) {
        log.info("Finding order with ID: {}", id);
        Optional<Order> byId = orderRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            log.error("Order with ID {} not found.", id);
            throw new IllegalArgumentException("Order not found");
        }
    }


    // Method to add a new order
    @Override
    public void addOrders(CreateOrderRequestDto createOrderRequestDto, int userId) {
        log.info("Adding a new order for user with ID: {}", userId);
        Order order = orderMapper.map(createOrderRequestDto);
        Optional<User> byId = userRepository.findById(userId);
        if (byId.isPresent()) {
            order.setUser(byId.get());
            Optional<Product> byId1 = productRepository.findById(createOrderRequestDto.getProductDto().getId());
            if (byId1.isPresent()) {
                order.setProduct(byId1.get());
            } else {
                log.error("Product with ID {} not found.", createOrderRequestDto.getProductDto().getId());
                throw new IllegalArgumentException("Product not found");
            }
            orderRepository.save(order);
        } else {
            log.error("User with ID {} not found.", userId);
            throw new IllegalArgumentException("User not found");
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

    // Method to delete an order by its ID
    @Override
    public void deleteById(int id) {
        log.info("Deleting order with ID: {}", id);
        orderRepository.deleteById(id);
    }

    // Method to update an existing order
    @Override
    public void update(int id, UpdateOrderRequestDto updateOrderRequestDto) {
        log.info("Updating order with ID: {}", id);
        Order order = orderMapper.updateDto(updateOrderRequestDto);
        Optional<Product> byId1 = productRepository.findById(updateOrderRequestDto.getProductDto().getId());
        if (byId1.isPresent()) {
            order.setProduct(byId1.get());
        } else {
            log.error("Product with ID {} not found.", updateOrderRequestDto.getProductDto().getId());
            throw new IllegalArgumentException("Product not found");
        }

        Optional<User> byUser = userRepository.findById(updateOrderRequestDto.getUserDto().getId());
        if (byUser.isPresent()) {
            order.setUser(byUser.get());
        } else {
            log.error("User with ID {} not found.", updateOrderRequestDto.getUserDto().getId());
            throw new IllegalArgumentException("User not found");
        }

        Optional<Order> byId = orderRepository.findById(id);
        if (byId.isPresent()) {
            Order orderDb = byId.get();
            orderDb.setUser(order.getUser());
            orderDb.setProduct(order.getProduct());
            orderDb.setOrderDate(order.getOrderDate());
            orderRepository.save(orderDb);
        } else {
            log.error("Order with ID {} not found.", id);
            throw new IllegalArgumentException("Order not found");
        }
    }


    // Method to get an order by its ID and return its DTO representation
    public OrderDto getOrderById(int id) {
        log.info("Getting order with ID: {}", id);
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return orderMapper.mapToDto(order.get());
        } else {
            log.error("Order with ID {} not found.", id);
            throw new IllegalArgumentException("Order not found");
        }
    }
}
