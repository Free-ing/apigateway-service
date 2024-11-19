package Freeing.apigateway_service.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
@Order(0) // 낮은 숫자가 먼저 실행됨
public class JwtAuthenticationWebFilter implements WebFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 인증이 필요 없는 경로 설정
        if (isExcludedPath(path)) {
            return chain.filter(exchange); // 필터를 통과시킴
        }

        String token = getTokenFromRequest(exchange);

        if (StringUtils.hasText(token) && validateToken(token)) {
            Claims claims = getClaims(token);
            System.out.println("JWT is valid. Claims: " + claims);

            // SecurityContext에 사용자 정보 저장
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);
            SecurityContext context = new SecurityContextImpl(authentication);

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
        } else {
            System.out.println("JWT is invalid or missing. Rejecting request.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
    private String getTokenFromRequest(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    // 인증 없이 접근 가능한 경로를 정의
    private boolean isExcludedPath(String path) {
        return path.startsWith("/user-service/login") ||
                path.startsWith("/user-service/signup") ||
                path.startsWith("/user-service/refresh-token") ||
                path.startsWith("/user-service/health_check") ||
                path.startsWith("/user-service/email/send-verification") ||
                path.startsWith("/user-service/email/verify")||
                path.startsWith("/user-service/check-email")||
                path.startsWith("/user-service/change-password/before-login")||
                path.startsWith("/health-check");
    }
}
