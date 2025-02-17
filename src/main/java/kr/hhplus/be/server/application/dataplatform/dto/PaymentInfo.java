package kr.hhplus.be.server.application.dataplatform.dto;

import kr.hhplus.be.server.domain.payment.model.Payment;

import java.time.LocalDateTime;

public record PaymentInfo(Long paymentId, LocalDateTime paidAt) {
    public static PaymentInfo from(Payment payment) {
        return new PaymentInfo(payment.getId(), payment.getPaidAt());
    }
}
