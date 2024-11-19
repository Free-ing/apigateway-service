package Freeing.apigateway_service.config;

import Freeing.apigateway_service.filter.JwtAuthenticationWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class GlobalJwtFilterConfig {

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    public GlobalJwtFilterConfig(JwtAuthenticationWebFilter jwtAuthenticationWebFilter) {
        this.jwtAuthenticationWebFilter = jwtAuthenticationWebFilter;
    }

    @Bean
    public WebFilter globalJwtWebFilter() {
        return jwtAuthenticationWebFilter;
    }
}
