package com.example.greenshopweb.service;

import com.example.greenshopcommon.dto.cartDto.CartDto;
import com.example.greenshopcommon.entity.Cart;
import com.example.greenshopcommon.entity.User;
import com.example.greenshopweb.security.CurrentUser;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CartService {

    List<Cart> findCarts(User user);

    Optional<Cart> findById(int id);

    void addCart(int productId, User user, int quantity) throws IOException;

    void deleteById(int id);

    public void updateCart(Cart cart);

    public List<CartDto> findCartsByUser(User user);

    public void updateCartByCurrentUser(int userId, int newQuantity,int cartId);

    double calculateCurrentUserCartTotal(CurrentUser currentUser);
}