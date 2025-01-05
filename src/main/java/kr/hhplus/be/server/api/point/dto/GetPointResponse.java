package kr.hhplus.be.server.api.point.dto;

public record GetPointResponse(
        long id,
        String name,
        long balance
) {
}
