package kr.hhplus.be.server.api.order.dto;

public record OrderItemRequest(
        long productId,
        int quantity
) {
}
