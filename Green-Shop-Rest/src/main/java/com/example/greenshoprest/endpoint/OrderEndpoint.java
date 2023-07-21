package com.example.greenshoprest.endpoint;


import com.example.greenshopcommon.dto.orderDto.CreateOrderRequestDto;
import com.example.greenshopcommon.dto.orderDto.OrderDto;
import com.example.greenshoprest.security.CurrentUser;
import com.example.greenshoprest.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderEndpoint {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable int id) {
        return orderService.getOrderById(id);

    }
    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@AuthenticationPrincipal CurrentUser currentUser) {
        return  orderService.getOrdersByUserId(currentUser.getUser());
    }

    @PostMapping()
    public ResponseEntity<?> addOrder(@RequestBody CreateOrderRequestDto createOrderRequestDto, @AuthenticationPrincipal CurrentUser currentUser) {
        return  orderService.addOrder(createOrderRequestDto,currentUser.getUser());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderById(@PathVariable int id) {
        return   orderService.deleteOrderById(id);
    }
}