package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.dto.cartDto.CreateCartRequestDto;
import com.example.greenshopcommon.dto.cartDto.UpdateCartRequestDto;
import com.example.greenshopcommon.exception.EntityNotFoundException;
import com.example.greenshopcommon.exception.IllegalArgumentExceptionError;
import com.example.greenshoprest.security.CurrentUser;
import com.example.greenshoprest.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class CartEndpoint {

    private final CartService cartService;

    @PostMapping()
    public ResponseEntity<CartDto> create(@AuthenticationPrincipal CurrentUser currentUser, @RequestBody CreateCartRequestDto requestDto) throws IOException, IllegalArgumentExceptionError {
        log.info("Creating a new cart for user: {}", currentUser.getUsername());
        return ResponseEntity.ok(cartService.addCart(currentUser.getUser(), requestDto).getBody());
    }

    @GetMapping()
    public ResponseEntity<List<CartDto>> getAll(@AuthenticationPrincipal CurrentUser currentUser) {
        log.info("Fetching all carts for user: {}", currentUser.getUsername());
        return ResponseEntity.ok(cartService.findCartsByUser(currentUser.getUser()).getBody());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartDto> getById(@PathVariable("id") int id) throws EntityNotFoundException {
        log.info("Fetching cart with ID: {}", id);
        return ResponseEntity.ok(cartService.findById(id).getBody());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartDto> update(@PathVariable("id") int id, @AuthenticationPrincipal CurrentUser currentUser, @RequestBody UpdateCartRequestDto updateCartRequestDto) {
        log.info("Updating cart with ID: {} for user: {}", id, currentUser.getUsername());
        return ResponseEntity.ok(cartService.updateCart(id, currentUser.getUser(), updateCartRequestDto).getBody());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") int id) {
        log.info("Deleting cart with ID: {}", id);
        return cartService.deleteById(id);
    }

}
