package kr.hhplus.be.server.global.exception;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse(500, e.getMessage()));
    }

    @ExceptionHandler(value = DomainException.class)
    public ResponseEntity<ErrorResponse> handleException(DomainException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(new ErrorResponse(e.getStatus(), e.getMessage()));
    }

    @ExceptionHandler(value = OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleException(OptimisticLockingFailureException e) {
        return ResponseEntity.status(409).body(new ErrorResponse(409, "데이터가 이미 변경되었습니다. 최신 데이터를 확인해주세요."));
    }
}
