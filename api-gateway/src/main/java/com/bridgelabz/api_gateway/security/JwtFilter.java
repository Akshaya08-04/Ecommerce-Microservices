package com.bridgelabz.api_gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        System.out.println("PATH: " + path);

        // ✅ PUBLIC APIs
        if (path.equals("/users/login") ||
                (path.equals("/users") && method == HttpMethod.POST)) {

            System.out.println("PUBLIC API HIT");
            return chain.filter(exchange);
        }

        // 🔐 GET AUTH HEADER
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        // ❌ NO TOKEN
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        // ❌ INVALID TOKEN
        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 🔐 EXTRACT ROLE
        String role = jwtUtil.getRole(token);

        // 🔥 ADMIN ONLY API
        if (path.startsWith("/users/delete") && !"ADMIN".equals(role)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        // ✅ ALLOW
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}