package com.shareday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class SharedayBeApplication {

    @Autowired
    private Environment env;   // ✅ Environment 주입

    public static void main(String[] args) {
        SpringApplication.run(SharedayBeApplication.class, args);
    }

    @PostConstruct
    public void checkEnv() {
        System.out.println("👉 KAKAO_CLIENT_ID = " + env.getProperty("KAKAO_CLIENT_ID"));
        System.out.println("👉 KAKAO_CLIENT_SECRET = " + env.getProperty("KAKAO_CLIENT_SECRET"));
    }

}
