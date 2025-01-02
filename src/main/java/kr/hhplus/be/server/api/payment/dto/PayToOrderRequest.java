package kr.hhplus.be.server.api.payment.dto;

import java.util.Optional;

public record PayToOrderRequest(
        long userId,
        long orderId,
        Optional<Long> couponId
) {
}
