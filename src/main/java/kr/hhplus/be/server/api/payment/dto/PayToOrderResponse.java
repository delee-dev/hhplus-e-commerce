package kr.hhplus.be.server.api.payment.dto;

import kr.hhplus.be.server.domain.order.model.OrderStatus;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;

import java.time.LocalDateTime;

public record PayToOrderResponse(
        long orderId,
        long paymentId,
        long amount,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        LocalDateTime paymentTime
) {
}
