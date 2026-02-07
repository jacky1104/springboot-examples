package com.fortinet.fortigatecloud.gateway.swagger.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class GetToPostTransformGatewayFilterFactory extends AbstractGatewayFilterFactory<GetToPostTransformGatewayFilterFactory.Config> {

    public GetToPostTransformGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Build new URI with default parameters
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUri(request.getURI())
                .queryParam("source", "gateway")
                .queryParam("version", "v1");

            // Add any additional default params from config
            if (config.getAdditionalParams() != null) {
                config.getAdditionalParams().forEach(uriBuilder::queryParam);
            }

            // Transform GET to POST and update the URI
            ServerHttpRequest mutatedRequest = request.mutate()
                .method(HttpMethod.POST)
                .uri(uriBuilder.build().toUri())
                .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    public static class Config {
        private java.util.Map<String, String> additionalParams;

        public java.util.Map<String, String> getAdditionalParams() {
            return additionalParams;
        }

        public void setAdditionalParams(java.util.Map<String, String> additionalParams) {
            this.additionalParams = additionalParams;
        }
    }
}
