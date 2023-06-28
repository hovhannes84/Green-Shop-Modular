package com.example.greenshopcommon.repository;


import com.example.greenshopcommon.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
