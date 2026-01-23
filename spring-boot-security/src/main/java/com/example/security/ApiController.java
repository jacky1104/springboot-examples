package com.example.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/public")
    public Message publicMessage() {
        return new Message("public ok");
    }

    @GetMapping("/hello")
    public Message hello() {
        return new Message("hello, user");
    }
}
