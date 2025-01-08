package kr.hhplus.be.server.application.payment.dto;

import java.util.Optional;

public record PaymentCommand(
        long userId,
        long orderId,
        Optional<Long> couponId
) {
}
