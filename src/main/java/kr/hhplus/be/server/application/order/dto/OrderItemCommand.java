package kr.hhplus.be.server.application.order.dto;

public record OrderItemCommand(
        long productId,
        int quantity
) {
}
