package kr.hhplus.be.server.api.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "포인트 충전 요청 DTO")
public record ChargePointRequest(
        @NotNull
        @Positive
        @Schema(description = "사용자 ID", example = "1")
        long userId,

        @NotNull
        @Positive
        @Schema(description = "충전 금액", example = "1000")
        long amount
){
}
