package com.example.greenshoprest.endpoint;


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
    public ResponseEntity<OrderDto> getOrderById(@PathVariable int id) {
        OrderDto orderDto = orderService.getOrderById(id);
        if (orderDto != null) {
            return ResponseEntity.ok(orderDto);
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(@AuthenticationPrincipal CurrentUser currentUser) {
        List<OrderDto> orders = orderService.getOrdersByUserId(currentUser.getUser());
        return ResponseEntity.ok(orders);
    }

//    @PostMapping()
//    public ResponseEntity<OrderDto> addOrder(@RequestBody CreateOrderRequestDto createOrderRequestDto,@AuthenticationPrincipal CurrentUser currentUser) {
//        OrderDto createdOrder = orderService.addOrder(createOrderRequestDto,currentUser.getUser());
//        return ResponseEntity.ok(createdOrder);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable int id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }

}