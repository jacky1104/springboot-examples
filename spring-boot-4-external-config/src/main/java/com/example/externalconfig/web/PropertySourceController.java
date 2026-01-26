package com.example.externalconfig.web;

import com.example.externalconfig.config.SimplePropertySourceExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/property-source")
public class PropertySourceController {

    @Autowired
    private SimplePropertySourceExample propertySourceExample;

    @GetMapping("/info")
    public Map<String, String> getPropertySourceInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "Properties loaded via @PropertySource annotation");
        info.put("appInfo", propertySourceExample.getAppInfo());
        return info;
    }
}