package com.example.greenshoprest.endpoint;

import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.dto.cartDto.CreateCartRequestDto;
import com.example.greenshopcommon.dto.cartDto.UpdateCartRequestDto;
import com.example.greenshoprest.security.CurrentUser;
import com.example.greenshoprest.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartEndpoint {

    private final CartService cartService;


    @PostMapping()
    public ResponseEntity<CartDto> create(@AuthenticationPrincipal CurrentUser currentUser,@RequestBody CreateCartRequestDto requestDto) throws IOException {
        return ResponseEntity.ok(cartService.addCart(currentUser.getUser(),requestDto).getBody());
    }

    @GetMapping()
    public ResponseEntity<List<CartDto>> getAll(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(cartService.findCartsByUser(currentUser.getUser()).getBody());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartDto> getById(@PathVariable("id") int id) {
        return ResponseEntity.ok(cartService.findById(id).getBody());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartDto> update(@PathVariable("id") int id, @AuthenticationPrincipal CurrentUser currentUser, @RequestBody UpdateCartRequestDto updateCartRequestDto) {
        return ResponseEntity.ok(cartService.updateCart(id, currentUser.getUser(),updateCartRequestDto).getBody());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") int id) {
        return cartService.deleteById(id);

    }

}
