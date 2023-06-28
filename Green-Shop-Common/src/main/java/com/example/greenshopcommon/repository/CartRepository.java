package com.example.greenshopcommon.repository;


import com.example.greenshopcommon.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findAllByUserId(int id);
    Optional<Cart> findByProductId(int productId);


}
