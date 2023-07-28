package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.orderDto.CreateOrderRequestDto;
import com.example.greenshopcommon.dto.orderDto.OrderDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.dto.userDto.UserDto;
import com.example.greenshopcommon.entity.Order;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopcommon.exception.EntityNotFoundException;
import com.example.greenshopcommon.mapper.CategoryMapper;
import com.example.greenshopcommon.mapper.OrderMapper;
import com.example.greenshopcommon.mapper.ProductMapper;
import com.example.greenshopcommon.mapper.UserMapper;
import com.example.greenshopcommon.repository.CartRepository;
import com.example.greenshopcommon.repository.OrderRepository;
import com.example.greenshopcommon.repository.ProductRepository;
import com.example.greenshopcommon.repository.UserRepository;
import com.example.greenshoprest.service.CartService;
import com.example.greenshoprest.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {


    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final CartRepository cartRepository;
    private final CartService cartService;


//    Fetches an order by its ID and returns the corresponding OrderDto.
    @Override
    public ResponseEntity<OrderDto> getOrderById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("The id cannot be 0 or less than 0: " + id);
        }
        log.info("Fetching order with ID: {}", id);
        return orderRepository.findById(id)
                .map(order -> {
                    OrderDto orderDto = orderMapper.mapToDto(order);
                    userRepository.findById(order.getUser().getId())
                            .ifPresent(user -> orderDto.setUserDto(userMapper.mapToDto(user)));
                    productRepository.findById(order.getProduct().getId())
                            .ifPresent(product -> {
                                ProductDto productDto = productMapper.mapToDto(product);
                                productDto.setCategoryDto(categoryMapper.mapToDto(product.getCategory()));
                                orderDto.setProductDto(productDto);
                            });
                    log.info("Order with ID {} fetched successfully", id);
                    return ResponseEntity.ok(orderDto);
                })
                .orElseThrow(() -> {
                    log.info("Order with ID {} not found", id);
                    throw new EntityNotFoundException("Order with ID " + id + " does not exist.");
                });
    }

// Fetches all orders for a given user and returns a list of OrderDtos.
    @Override
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null.");
        }
        log.info("Fetching orders for user with ID: {}", user.getId());
        List<Order> allByUserId = orderRepository.findAllByUserId(user.getId());
        List<OrderDto> allOrderDtos = allByUserId.stream()
                .map(order -> {
                    OrderDto orderDto = orderMapper.mapToDto(order);
                    UserDto userDto = userMapper.mapToDto(user);
                    orderDto.setUserDto(userDto);
                    ProductDto productDto = productMapper.mapToDto(order.getProduct());
                    productDto.setCategoryDto(categoryMapper.mapToDto(order.getProduct().getCategory()));
                    orderDto.setProductDto(productDto);
                    return orderDto;
                })
                .collect(Collectors.toList());
        if (allOrderDtos.isEmpty()) {
            log.info("No orders found for user with ID: {}", user.getId());
            throw new EntityNotFoundException("No orders found for user with ID " + user.getId() + ".");
        } else {
            log.info("Fetched {} orders for user with ID: {}", allOrderDtos.size(), user.getId());
            return ResponseEntity.ok(allOrderDtos);
        }
    }
//     Adds a new order for the given user with the provided order details.
    @Override
    public ResponseEntity<OrderDto> addOrder(CreateOrderRequestDto createOrderRequestDto, User user) {
        if (user == null || createOrderRequestDto == null) {
            throw new IllegalArgumentException("User and createOrderRequestDto must not be null.");
        }
        log.info("Adding a new order for user with ID: {}", user.getId());
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + user.getId() + " not found."));
        Product product = productRepository.findById(createOrderRequestDto.getProductDto().getId())
                .orElseThrow(() -> new EntityNotFoundException("Product with ID " + createOrderRequestDto.getProductDto().getId() + " not found."));
        Order order = orderMapper.map(createOrderRequestDto);
        order.setUser(existingUser);
        order.setProduct(product);
        orderRepository.save(order);
        cartRepository.findByProductId(product.getId()).ifPresent(cart -> {
            Product cartProduct = cart.getProduct();
            cartProduct.setQuantity(cartProduct.getQuantity() - cart.getQuantity());
            productRepository.save(cartProduct);
            cartService.deleteById(cart.getId());
        });
        log.info("New order added successfully for user with ID: {}", user.getId());
        return ResponseEntity.ok(orderMapper.mapToDto(order));
    }

//     Deletes an order by its ID.
    @Override
    public ResponseEntity<?> deleteOrderById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("The id cannot be 0 or less than 0: " + id);
        }
        log.info("Deleting order with ID: {}", id);
        Optional<Order> byId = orderRepository.findById(id);
        if (byId.isEmpty()){
            log.info("Order with ID {} not found for deletion", id);
            throw new EntityNotFoundException("Order with ID " + id + " does not exist.");
        }
        orderRepository.deleteById(id);
        log.info("Order with ID {} deleted", id);
        return ResponseEntity.noContent().build();
    }

}
