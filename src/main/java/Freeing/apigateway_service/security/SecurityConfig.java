package Freeing.apigateway_service.security;

import Freeing.apigateway_service.filter.JwtAuthenticationWebFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Autowired
    public SecurityConfig(JwtAuthenticationWebFilter jwtAuthenticationWebFilter, CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationWebFilter = jwtAuthenticationWebFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        // WebFlux 환경에서 CORS 설정
        http.cors(cors -> cors.configurationSource(corsConfigurationSource));

        // 특정 경로에 대해 인증 없이 접근 가능하게 설정
        http.authorizeExchange(exchange -> exchange
                .pathMatchers(
                        "/user-service/login",
                        "/user-service/signup",
                        "/user-service/refresh-token",
                        "/user-service/health_check",
                        "/user-service/email/send-verification",
                        "/user-service/email/verify",
                        "/user-service/check-email",
                        "/user-service/change-password/before-login",
                        "/health-check"
                ).permitAll()
                .anyExchange().authenticated()
        );

        // JWT 필터 추가
            http.addFilterBefore(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}
