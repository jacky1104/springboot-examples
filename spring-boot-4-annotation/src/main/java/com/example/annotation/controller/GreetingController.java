package com.example.annotation.controller;

import com.example.annotation.Traceable;
import com.example.annotation.service.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @Traceable("controller.greet")
    @GetMapping("/api/greet")
    public String greet(@RequestParam(defaultValue = "Spring") String name) {
        return greetingService.greet(name);
    }
}
