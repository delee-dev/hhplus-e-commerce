package kr.hhplus.be.server.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.domain.order.model.OrderStatus;

import java.time.LocalDateTime;

@Schema(description = "주문 생성 응답 DTO")
public record OrderResponse(
        @Schema(description = "주문 ID", example = "1")
        long orderId,
        @Schema(description = "관련된 결제 ID", example = "1")
        long paymentId,
        @Schema(description = "주문 상태", example = "PAYMENT_PENDING")
        OrderStatus orderStatus,
        @Schema(description = "주문 시간", example = "2025-01-01T12:00:00")
        LocalDateTime orderTime
) {
        public static OrderResponse from(OrderResult orderResult) {
                return new OrderResponse(
                        orderResult.orderId(),
                        orderResult.paymentId(),
                        orderResult.orderStatus(),
                        orderResult.orderTime()
                );
        }
}
