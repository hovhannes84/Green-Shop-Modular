package com.example.greenshoprest.endpoint;


import com.example.greenshopcommon.dto.orderDto.CreateOrderRequestDto;
import com.example.greenshopcommon.dto.orderDto.OrderDto;
import com.example.greenshoprest.security.CurrentUser;
import com.example.greenshoprest.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderEndpoint {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable int id) {
        log.info("Fetching order with ID: {}", id);
        return orderService.getOrderById(id);

    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@AuthenticationPrincipal CurrentUser currentUser) {
        log.info("Fetching orders for user with ID: {}", currentUser.getUser().getId());
        return orderService.getOrdersByUserId(currentUser.getUser());
    }

    @PostMapping()
    public ResponseEntity<?> addOrder(@RequestBody CreateOrderRequestDto createOrderRequestDto, @AuthenticationPrincipal CurrentUser currentUser) {
        log.info("Adding a new order for user with ID: {}", currentUser.getUser().getId());
        return orderService.addOrder(createOrderRequestDto, currentUser.getUser());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderById(@PathVariable int id) {
        log.info("Deleting order with ID: {}", id);
        return orderService.deleteOrderById(id);
    }
}