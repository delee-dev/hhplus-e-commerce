package kr.hhplus.be.server.application.payment.dto;

import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.order.model.OrderStatus;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResult(
        long orderId,
        long paymentId,
        long amount,
        OrderStatus orderStatus,
        PaymentStatus paymentStatus,
        LocalDateTime paymentTime
) {
    public static PaymentResult from(Order order, Payment payment) {
        return new PaymentResult(
                order.getId(),
                payment.getId(),
                payment.getFinalAmount(),
                order.getStatus(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
