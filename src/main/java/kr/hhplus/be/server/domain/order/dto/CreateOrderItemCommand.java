package kr.hhplus.be.server.domain.order.dto;

public record CreateOrderItemCommand(
        Long productId,
        String productName,
        Long price,
        int quantity
) {
}
