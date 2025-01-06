package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum ProductErrorCode implements ErrorCode {
    PRODUCT_OUT_OF_STOCK(400, "상품의 재고가 부족합니다.");

    private int status;
    private String message;

    ProductErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
