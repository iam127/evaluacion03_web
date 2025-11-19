package com.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestaurantApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);
        System.out.println("========================================");
        System.out.println("   SISTEMA SABOR GOURMET INICIADO");
        System.out.println("   http://localhost:8080");
        System.out.println("========================================");
    }
}