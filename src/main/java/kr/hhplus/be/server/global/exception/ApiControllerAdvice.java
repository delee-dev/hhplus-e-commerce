package kr.hhplus.be.server.global.exception;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse(500, e.getMessage()));
    }

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(new ErrorResponse(e.getCode(), e.getStatus(), e.getMessage()));
    }

    @ExceptionHandler(value = OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockException(OptimisticLockingFailureException e) {
        return ResponseEntity.status(409).body(new ErrorResponse(409, "데이터가 이미 변경되었습니다. 최신 데이터를 확인해주세요."));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> errorFields = fieldErrors.stream()
                .map(FieldError::getField).toList();

        return ResponseEntity.status(400).body(new ErrorResponse(400, String.format("%s 입력값 검증에 실패했습니다.", errorFields)));
    }
}
