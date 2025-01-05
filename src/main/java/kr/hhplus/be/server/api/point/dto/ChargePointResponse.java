package kr.hhplus.be.server.api.point.dto;

public record ChargePointResponse(
        long userId,
        String name,
        long balance
) {
}
