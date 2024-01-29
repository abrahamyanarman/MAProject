package com.song.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("song-service", ssr -> ssr.path("/songs/**")
                        .uri("lb://song-service"))
                .route("resource-service", rsr -> rsr.path("/resources/**")
                        .uri("lb://resource-service"))
                .build();
    }
}
