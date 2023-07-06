package com.example.greenshopweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;



@ComponentScan(basePackages = {"com.example.greenshopweb","com.example.greenshopcommon"})
@EntityScan("com.example.greenshopcommon.entity")
@EnableJpaRepositories(basePackages = "com.example.greenshopcommon.repository")
@SpringBootApplication
@EnableAsync
public class GreenShopWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreenShopWebApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
