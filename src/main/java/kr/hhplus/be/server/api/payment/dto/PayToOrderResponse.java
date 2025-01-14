package kr.hhplus.be.server.api.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.payment.dto.PaymentResult;
import kr.hhplus.be.server.domain.order.model.OrderStatus;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;

import java.time.LocalDateTime;

@Schema(description = "주문 결제 응답 DTO")
public record PayToOrderResponse(
        @Schema(description = "주문 ID", example = "1")
        Long orderId,
        @Schema(description = "결제 ID", example = "1")
        Long paymentId,
        @Schema(description = "결제할 총 금액", example = "15000")
        Long amount,
        @Schema(description = "주문 상태", example = "PAYMENT_COMPLETED")
        OrderStatus orderStatus,
        @Schema(description = "결제 상태", example = "COMPLETED")
        PaymentStatus paymentStatus,
        @Schema(description = "결제 시간", example = "2025-01-01T12:00:00")
        LocalDateTime paymentTime
) {
        public static PayToOrderResponse from(PaymentResult paymentResult) {
                return new PayToOrderResponse(
                        paymentResult.orderId(),
                        paymentResult.paymentId(),
                        paymentResult.amount(),
                        paymentResult.orderStatus(),
                        paymentResult.paymentStatus(),
                        paymentResult.paymentTime()
                );
        }
}
