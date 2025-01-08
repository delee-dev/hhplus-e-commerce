package kr.hhplus.be.server.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.order.dto.OrderItemCommand;

@Schema(description = "주문 상품 DTO")
public record OrderItemRequest(
        @Schema(description = "상품 ID", example = "1")
        long productId,
        @Schema(description = "주문 수량", example = "3")
        int quantity
) {
        public OrderItemCommand toApp() {
                return new OrderItemCommand(productId, quantity);
        }
}
