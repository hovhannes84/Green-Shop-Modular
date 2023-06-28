package com.example.greenshopcommon.repository;


import com.example.greenshopcommon.entity.Ratingsreview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingsreviewRepository extends JpaRepository<Ratingsreview, Integer> {

    List<Ratingsreview> findAllByProductId(int id);
    Optional<Ratingsreview> findRatingsreviewByUserId(int id);

}
