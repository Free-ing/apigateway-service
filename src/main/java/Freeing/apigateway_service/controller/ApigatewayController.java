package Freeing.apigateway_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApigatewayController {

    @GetMapping("/health-check")
    public ResponseEntity<String> status(){
        return ResponseEntity.status(HttpStatus.OK).body("ApiGateway 정상 작동");
    }
}
