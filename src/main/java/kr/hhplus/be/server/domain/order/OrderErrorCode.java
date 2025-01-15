package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND("ORDER_001", 404, "존재하지 않는 주문입니다.");

    private String code;
    private int status;
    private String message;

    OrderErrorCode(String code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
