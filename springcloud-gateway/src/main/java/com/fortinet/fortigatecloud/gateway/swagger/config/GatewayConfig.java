package com.fortinet.fortigatecloud.gateway.swagger.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Configuration
public class GatewayConfig {

    private static final Logger log = LoggerFactory.getLogger(GatewayConfig.class);

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        log.info("Loading custom routes...");

        return builder.routes()
            .route("forticonverter-route", r -> r
                .path("/api/v1/public/forticonverter/**")
                .filters(f -> f
                    .rewritePath("/api/v1/public/forticonverter/?(?<segment>.*)", "/api/v2/forticonverter/customerGetAll")
                    .filter(getToPostTransformFilter()))
                .uri("https://172.16.95.47"))
            .build();
    }

    private GatewayFilter getToPostTransformFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Build new URI with additional parameters
            URI newUri = UriComponentsBuilder.fromUri(request.getURI())
                .queryParam("source", "gateway")
                .queryParam("version", "v1")
                .build()
                .toUri();

            // Transform GET to POST and add params
            ServerHttpRequest mutatedRequest = request.mutate()
                .method(HttpMethod.POST)
                .uri(newUri)
                .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }
}
