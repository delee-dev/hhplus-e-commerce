package kr.hhplus.be.server.global.exception;

public record ErrorResponse(
        String code,
        String message
) {
}
