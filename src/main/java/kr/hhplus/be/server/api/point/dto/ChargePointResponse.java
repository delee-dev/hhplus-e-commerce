package kr.hhplus.be.server.api.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 충전 응답 DTO")
public record ChargePointResponse(
        @Schema(description = "사용자 ID", example = "1")
        long userId,
        @Schema(description = "사용자 이름", example = "홍길동")
        String name,
        @Schema(description = "충전 후 잔액", example = "15000")
        long balance
) {
}
