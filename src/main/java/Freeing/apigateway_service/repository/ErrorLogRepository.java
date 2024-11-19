package Freeing.apigateway_service.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorLogRepository extends ReactiveCrudRepository<ErrorLog, Long> {
}
