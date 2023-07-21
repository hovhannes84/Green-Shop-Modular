package com.example.greenshoprest.service.impl;

import com.example.greenshopcommon.dto.orderDto.CreateOrderRequestDto;
import com.example.greenshopcommon.dto.orderDto.OrderDto;
import com.example.greenshopcommon.dto.productDto.ProductDto;
import com.example.greenshopcommon.entity.Cart;
import com.example.greenshopcommon.entity.Order;
import com.example.greenshopcommon.entity.Product;
import com.example.greenshopcommon.entity.User;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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

    @Override
    public ResponseEntity<OrderDto> getOrderById(int id) {
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
                    return ResponseEntity.ok(orderDto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(User user) {
        List<Order> allByUserId = orderRepository.findAllByUserId(user.getId());
        List<OrderDto> allOrderDtos = new ArrayList<>();
        for (Order order : allByUserId) {
            OrderDto orderDto = orderMapper.mapToDto(order);
            orderDto.setUserDto(userMapper.mapToDto(user));
            ProductDto productDto = productMapper.mapToDto(order.getProduct());
            productDto.setCategoryDto(categoryMapper.mapToDto(order.getProduct().getCategory()));
            orderDto.setProductDto(productDto);
            allOrderDtos.add(orderDto);
        }
        if (allOrderDtos.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(allOrderDtos);
        }
    }


    @Override
    public ResponseEntity<OrderDto> addOrder(CreateOrderRequestDto createOrderRequestDto, User user) {
        Optional<User> byId = userRepository.findById(user.getId());
        Optional<Product> product = productRepository.findById(createOrderRequestDto.getProductDto().getId());
        if (byId.isPresent() && product.isPresent()) {
            Order order = orderMapper.map(createOrderRequestDto);
            order.setUser(byId.get());
            order.setProduct(product.get());
            orderRepository.save(order);
            Optional<Cart> byProductId = cartRepository.findByProductId(product.get().getId());
            byProductId.ifPresent(cart -> {
                Product cartProduct = cart.getProduct();
                cartProduct.setQuantity(cartProduct.getQuantity() - cart.getQuantity());
                productRepository.save(cartProduct);
                cartService.deleteById(cart.getId());
            });
            return ResponseEntity.ok(orderMapper.mapToDto(order));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<?> deleteOrderById(int id) {
        Optional<Order> byId = orderRepository.findById(id);
        if (byId.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        orderRepository.deleteById(id);
        return ResponseEntity.notFound().build();
    }

}
