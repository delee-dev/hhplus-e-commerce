package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum PaymentErrorCode implements ErrorCode {
    PAYMENT_ALREADY_COMPLETED(409, "이미 결제 완료된 주문입니다."),
    PAYMENT_ALREADY_CANCELLED(409, "이미 취소된 주문입니다."),
    PAYMENT_NOT_FOUNT(404, "존재하지 않는 결제입니다.");

    private int status;
    private String message;

    PaymentErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
