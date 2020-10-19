package com.example.javatestproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class JavatestprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavatestprojectApplication.class, args);
    }

}
