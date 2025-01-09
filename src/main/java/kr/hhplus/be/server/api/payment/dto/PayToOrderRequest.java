package kr.hhplus.be.server.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.payment.dto.PaymentCommand;

import java.util.Optional;

@Schema(description = "주문 결제 요청 DTO")
public record PayToOrderRequest(
        @NotNull
        @Positive
        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @NotNull
        @Positive
        @Schema(description = "주문 ID", example = "1")
        Long orderId,

        @Schema(description = "적용할 쿠폰 ID", nullable = true, example = "1")
        Optional<Long> couponId
) {
        public PaymentCommand toApp() {
                return new PaymentCommand(userId, orderId, couponId);
        }
}
