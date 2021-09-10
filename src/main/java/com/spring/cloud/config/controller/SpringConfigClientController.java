package com.spring.cloud.config.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RefreshScope
public class SpringConfigClientController {

//    @Value("${configuration.name}")
//    private String name;
//
//    @Value("${configuration.age}")
//    private String age;
//
//    @GetMapping("/info")
//    public String getUserInfo() {
//        return String.format("Hello %s, You are %s years old", name, age);
//    }

    @GetMapping("/dockerize")
    public String dockerizeApp() {
        return "I have dockerized this app";
    }

}
