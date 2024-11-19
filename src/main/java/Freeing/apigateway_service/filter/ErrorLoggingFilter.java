package Freeing.apigateway_service.filter;

import Freeing.apigateway_service.repository.ErrorLog;
import Freeing.apigateway_service.repository.ErrorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@Order(-1) // 필터의 실행 순서 설정
public class ErrorLoggingFilter implements GlobalFilter {

    private final ErrorLogRepository errorLogRepository;

    @Autowired
    public ErrorLoggingFilter(ErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .doOnError(throwable -> logError(exchange, throwable))
                .then(Mono.defer(() -> {
                    HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
                    if (statusCode != null && statusCode.isError()) {
                        return logErrorResponse(exchange, statusCode);
                    }
                    return Mono.empty();
                }));
    }

    private void logError(ServerWebExchange exchange, Throwable throwable) {
        ErrorLog errorLog = new ErrorLog();
        errorLog.setRequestUri(exchange.getRequest().getURI().toString());
        errorLog.setStatusCode(500); // 예외 발생 시 기본 오류 코드
        errorLog.setErrorMessage(throwable.getMessage());
        errorLog.setRequestHeaders(exchange.getRequest().getHeaders().toString());
        errorLog.setTimestamp(LocalDateTime.now());

        errorLogRepository.save(errorLog).subscribe(); // 비동기 저장
        System.out.println("Error occurred: " + throwable.getMessage());
    }

    private Mono<Void> logErrorResponse(ServerWebExchange exchange, HttpStatusCode statusCode) {
        ErrorLog errorLog = new ErrorLog();
        errorLog.setRequestUri(exchange.getRequest().getURI().toString());
        errorLog.setStatusCode(statusCode.value());
        errorLog.setErrorMessage("Error response detected");
        errorLog.setRequestHeaders(exchange.getRequest().getHeaders().toString());
        errorLog.setTimestamp(LocalDateTime.now());

        System.out.println("Error response detected for URI: " + exchange.getRequest().getURI());
        return errorLogRepository.save(errorLog).then();
    }
}
