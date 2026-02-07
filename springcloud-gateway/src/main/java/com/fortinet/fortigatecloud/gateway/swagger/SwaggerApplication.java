package com.fortinet.fortigatecloud.gateway.swagger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.fortinet.fortigatecloud.gateway.swagger",
    "com.fortinet.fortigatecloud.gateway.swagger.filter"
})
public class SwaggerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwaggerApplication.class, args);
	}

}
