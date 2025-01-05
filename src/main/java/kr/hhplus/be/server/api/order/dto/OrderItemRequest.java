package kr.hhplus.be.server.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 상품 DTO")
public record OrderItemRequest(
        @Schema(description = "상품 ID", example = "1")
        long productId,
        @Schema(description = "주문 수량", example = "3")
        int quantity
) {
}
