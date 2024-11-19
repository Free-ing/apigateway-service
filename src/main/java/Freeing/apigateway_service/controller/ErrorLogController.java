package Freeing.apigateway_service.controller;

import Freeing.apigateway_service.repository.ErrorLog;
import Freeing.apigateway_service.repository.ErrorLogRepository;
import Freeing.apigateway_service.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
public class ErrorLogController {

    private final ErrorLogRepository errorLogRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public ErrorLogController(ErrorLogRepository errorLogRepository, JwtTokenProvider jwtTokenProvider) {
        this.errorLogRepository = errorLogRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/admin/error-logs")
    public Mono<String> viewErrorLogs(Model model,
                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        // JWT 토큰 검증 및 역할 확인
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자만 접속 가능합니다."));
        }

        String token = authorizationHeader.substring(7);
        int role = jwtTokenProvider.getRoleFromToken(token);
        if (role != 0) {
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자만 접속 가능합니다."));
        }

        // 모든 오류 로그 조회 후 List로 변환하여 모델에 추가
        return errorLogRepository.findAll().collectList()
                .flatMap(errorLogs -> {
                    model.addAttribute("errorLogs", errorLogs); // 모델에 List<ErrorLog> 추가
                    return Mono.just("error_logs"); // 뷰 이름 반환
                });
    }
}
