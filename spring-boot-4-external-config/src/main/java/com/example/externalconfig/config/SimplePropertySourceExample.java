package com.example.externalconfig.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:app.properties")
public class SimplePropertySourceExample {

    @Value("${app.name:DefaultApp}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.description:Simple PropertySource Example}")
    private String appDescription;

    public String getAppInfo() {
        return String.format("App: %s v%s - %s", appName, appVersion, appDescription);
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAppDescription() {
        return appDescription;
    }
}