package com.example.annotation.service;

import com.example.annotation.Traceable;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    @Traceable("service.greet")
    public String greet(String name) {
        return "Hello, " + name + "!";
    }
}
