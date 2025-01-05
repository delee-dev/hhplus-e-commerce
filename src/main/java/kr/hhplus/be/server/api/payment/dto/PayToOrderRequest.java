package kr.hhplus.be.server.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

@Schema(description = "주문 결제 요청 DTO")
public record PayToOrderRequest(
        @Schema(description = "사용자 ID", example = "1")
        long userId,
        @Schema(description = "주문 ID", example = "1")
        long orderId,
        @Schema(description = "적용할 쿠폰 ID", nullable = true, example = "1")
        Optional<Long> couponId
) {
}
