package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum PaymentErrorCode implements ErrorCode {
    PAYMENT_ALREADY_COMPLETED("PAYMENT_001", 409, "이미 결제 완료된 주문입니다."),
    PAYMENT_ALREADY_CANCELLED("PAYMENT_002", 409, "이미 취소된 주문입니다."),
    PAYMENT_NOT_FOUNT("PAYMENT_003", 404, "존재하지 않는 결제입니다.");

    private String code;
    private int status;
    private String message;

    PaymentErrorCode(String code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
