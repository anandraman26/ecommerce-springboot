package com.ecommerce.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class CustomRouteFilter extends AbstractGatewayFilterFactory<Object> {
    public CustomRouteFilter() {
        super(Object.class);
    }

    @Override
    public GatewayFilter apply(Object config) {
        // TODO Auto-generated method stub
        return (exchange, chain) -> {
            System.out.println("Route filter: Before routing");
            ServerHttpRequest request = exchange.getRequest().mutate().header("X-Route-Header", "Added-By-Gateway")
                    .build();
            return chain.filter(
                    exchange.mutate().request(request).build()
            ).then(Mono.fromRunnable(() -> {
                System.out.println("Route filter: After routing");
            }));
        };
    }
}
