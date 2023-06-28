package com.example.greenshoprest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@ComponentScan(basePackages = {"com.example.greenshoprest","com.example.greenshopcommon"})
@EntityScan("com.example.greenshopcommon.entity")
@EnableJpaRepositories(basePackages = "com.example.greenshopcommon.repository")
@SpringBootApplication
public class GreenShopRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreenShopRestApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
