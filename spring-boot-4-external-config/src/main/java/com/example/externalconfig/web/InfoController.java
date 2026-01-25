package com.example.externalconfig.web;

import com.example.externalconfig.config.AppProperties;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private final AppProperties properties;

    public InfoController(AppProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
                "greeting", properties.greeting(),
                "owner", properties.owner()
        );
    }
}
