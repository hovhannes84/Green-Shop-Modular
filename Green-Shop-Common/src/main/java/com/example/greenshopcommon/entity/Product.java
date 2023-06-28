package com.example.greenshopcommon.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product")
public class Product  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private  double price;
    private String description;
    private String image;
    @ManyToOne
    private Category category;
    private int quantity;
    private Double rating;

}