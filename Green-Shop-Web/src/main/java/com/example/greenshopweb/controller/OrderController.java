package com.example.greenshopweb.controller;


import com.example.greenshopcommon.dto.orderDto.CreateOrderRequestDto;
import com.example.greenshopcommon.dto.orderDto.UpdateOrderRequestDto;
import com.example.greenshopcommon.repository.UserRepository;
import com.example.greenshopweb.service.OrderService;
import com.example.greenshopweb.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;


    @GetMapping("/{id}")
    public String getOrderById(@PathVariable int id, ModelMap modelMap) {
        modelMap.addAttribute("order", orderService.getOrderById(id));
        return "singleOrder";
    }

    @GetMapping
    public String getOrdersByUserId(@AuthenticationPrincipal CurrentUser currentUser, ModelMap modelMap) {
        modelMap.addAttribute("orders", orderService.findOrdersByUser(currentUser.getUser().getId()));
        return "orderPage";

    }

    @PostMapping
    public String addOrder(@RequestBody CreateOrderRequestDto createOrderRequestDto, @RequestParam("userId") int userId) {
        orderService.addOrders(createOrderRequestDto, userId);
        return "redirect:/order";
    }

    @DeleteMapping("/{id}")
    public String deleteOrderById(@PathVariable int id) {
        orderService.deleteById(id);
        return "redirect:/order";
    }

    @PostMapping("/update/{id}")
    public String updateOrder(@PathVariable int id, @ModelAttribute UpdateOrderRequestDto updatedOrder) {
        orderService.update(id, updatedOrder);
        return "redirect:/order/";
    }

}
