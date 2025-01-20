package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum PointErrorCode implements ErrorCode {
    POINT_CHARGE_EXCEEDS_MAXIMUM("POINT_001", 400, "1회 최대 충전 한도를 초과했습니다."),
    POINT_CHARGE_BELOW_MINIMUM("POINT_002", 400, "1회 최소 충전 금액을 미달했습니다."),
    POINT_BALANCE_EXCEEDS_LIMIT("POINT_003", 400, "포인트 보유 한도를 초과하였습니다."),
    POINT_BALANCE_NOT_FOUND("POINT_004", 404, "회원 정보를 찾을 수 없습니다."),
    POINT_BALANCE_INSUFFICIENT("POINT_005", 400, "포인트가 부족합니다.");

    private String code;
    private int status;
    private String message;

    PointErrorCode(String code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
