package kr.hhplus.be.server.global.exception;

public interface ErrorCode {
    String getCode();
    int getStatus();
    String getMessage();
}
