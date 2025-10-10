package com.shareday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class SharedayBeApplication {
    public static void main(String[] args) {
        SpringApplication.run(SharedayBeApplication.class, args);
    }
}

