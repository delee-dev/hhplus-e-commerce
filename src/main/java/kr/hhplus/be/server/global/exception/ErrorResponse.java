package kr.hhplus.be.server.global.exception;

public class ErrorResponse{
    private String traceId;
    private String code;
    private int status;
    private String message;

    public ErrorResponse(String traceId, String code, int status, String message) {
        this.traceId = traceId;
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public ErrorResponse(String traceId, int status, String message) {
        this.traceId = traceId;
        this.status = status;
        this.message = message;
    }
}
