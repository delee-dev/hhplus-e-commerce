package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(404, "유저가 존재하지 않습니다.");

    private int status;
    private String message;

    UserErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
