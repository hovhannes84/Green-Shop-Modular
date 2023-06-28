package com.example.greenshopcommon.repository;


import com.example.greenshopcommon.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
