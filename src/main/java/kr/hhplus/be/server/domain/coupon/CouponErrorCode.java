package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum CouponErrorCode implements ErrorCode {
    COUPON_STOCK_DEPLETED("COUPON_001", 410, "쿠폰이 모두 소진되었습니다."),
    COUPON_ALREADY_ISSUED("COUPON_002",  409, "이미 쿠폰을 발급 받은 사용자 입니다."),
    COUPON_ISSUE_IN_PROGRESS("COUPON_003", 409, "이미 발급 대기 중입니다."),
    COUPON_EXPIRED("COUPON_004", 400, "만료된 쿠폰입니다."),
    COUPON_INVALID("COUPON_005", 409, "유효하지 않은 쿠폰입니다."),
    COUPON_ALREADY_USED("COUPON_006", 400, "이미 사용된 쿠폰입니다."),
    COUPON_NOT_APPLICABLE_TO_PAYMENT("COUPON_007", 400, "해당 결제에 적용 불가능한 쿠폰 입니다."),
    COUPON_NOT_FOUND("COUPON_008", 404, "존재하지 않는 쿠폰입니다.");

    private String code;
    private int status;
    private String message;

    CouponErrorCode(String code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
