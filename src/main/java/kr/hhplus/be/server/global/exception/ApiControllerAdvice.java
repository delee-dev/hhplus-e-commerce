package kr.hhplus.be.server.global.exception;

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
}
