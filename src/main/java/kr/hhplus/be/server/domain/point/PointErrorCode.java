package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum PointErrorCode implements ErrorCode {
    POINT_CHARGE_EXCEEDS_MAXIMUM(400, "1회 최대 충전 한도를 초과했습니다."),
    POINT_CHARGE_BELOW_MINIMUM(400, "1회 최소 충전 금액을 미달했습니다."),
    POINT_BALANCE_EXCEEDS_LIMIT(400, "포인트 보유 한도를 초과하였습니다."),
    POINT_BALANCE_NOT_FOUND(404, "회원 정보를 찾을 수 없습니다."),
    POINT_BALANCE_INSUFFICIENT(400, "포인트가 부족합니다.");

    private int status;
    private String message;

    PointErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
