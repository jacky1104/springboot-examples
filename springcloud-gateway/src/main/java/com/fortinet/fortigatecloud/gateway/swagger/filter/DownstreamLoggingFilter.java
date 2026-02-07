package com.fortinet.fortigatecloud.gateway.swagger.filter;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Component
public class DownstreamLoggingFilter implements GlobalFilter, Ordered {
	private static final Logger log = LoggerFactory.getLogger(DownstreamLoggingFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		logRequest(exchange, request);
		return chain.filter(exchange)
			.doOnSuccess(ignored -> logResponse(exchange));
	}

	private void logRequest(ServerWebExchange exchange, ServerHttpRequest request) {
		URI downstream = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
		Object route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
		log.info("Gateway forward: method={} path={} route={} downstream={}",
			request.getMethod(), request.getURI().getRawPath(), route, downstream);
	}

	private void logResponse(ServerWebExchange exchange) {
		if (exchange.getResponse().getStatusCode() == null) {
			return;
		}
		log.info("Gateway response: status={}", exchange.getResponse().getStatusCode().value());
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
