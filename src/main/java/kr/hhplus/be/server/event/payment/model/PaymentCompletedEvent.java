package kr.hhplus.be.server.event.payment.model;

import kr.hhplus.be.server.domain.payment.model.Payment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentCompletedEvent {
    private Long paymentId;
    private Long orderId;
    private Long totalAmount;
    private Long discountAmount;
    private Long finalAmount;
    private LocalDateTime paidAt;

    public static PaymentCompletedEvent from(Payment payment) {
        PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent();

        paymentCompletedEvent.paymentId = payment.getId();
        paymentCompletedEvent.orderId = payment.getOrderId();
        paymentCompletedEvent.totalAmount = payment.getTotalAmount();
        paymentCompletedEvent.discountAmount = payment.getDiscountAmount();
        paymentCompletedEvent.finalAmount = payment.getFinalAmount();
        paymentCompletedEvent.paidAt = payment.getPaidAt();

        return paymentCompletedEvent;
    }
}
