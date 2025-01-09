package kr.hhplus.be.server.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.order.dto.OrderItemCommand;

@Schema(description = "주문 상품 DTO")
public record OrderItemRequest(
        @NotNull
        @Positive
        @Schema(description = "상품 ID", example = "1")
        Long productId,

        @NotNull
        @Min(1)
        @Schema(description = "주문 수량", example = "3")
        int quantity
) {
        public OrderItemCommand toApp() {
                return new OrderItemCommand(productId, quantity);
        }
}
