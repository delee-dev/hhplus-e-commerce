package kr.hhplus.be.server.api.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 충전 요청 DTO")
public record ChargePointRequest(
        @Schema(description = "사용자 ID", example = "1")
        long userId,
        @Schema(description = "충전 금액", example = "1000")
        long amount
){
}
