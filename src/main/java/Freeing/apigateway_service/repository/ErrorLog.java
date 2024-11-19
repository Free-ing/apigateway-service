package Freeing.apigateway_service.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table("error_logs")  // 테이블 이름을 명시적으로 지정
public class ErrorLog {

    @Id
    @Column("error_log_id")
    private Long errorLogId;

    @Column("request_uri")
    private String requestUri;

    @Column("status_code")
    private Integer statusCode;

    @Column("error_message")
    private String errorMessage;

    @Column("request_headers")
    private String requestHeaders;

    @Column("timestamp")
    private LocalDateTime timestamp = LocalDateTime.now(); // 기본 값으로 현재 시간 설정
}
