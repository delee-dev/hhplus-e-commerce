package kr.hhplus.be.server.global.exception;

public class ErrorResponse{
    private String code;
    private int status;
    private String message;

    public ErrorResponse(String code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
