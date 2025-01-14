package kr.hhplus.be.server.api.order.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.order.dto.OrderCommand;
import kr.hhplus.be.server.application.order.dto.OrderItemCommand;

import java.util.List;

@Schema(description = "주문 생성 요청 DTO")
public record OrderRequest(
        @NotNull
        @Positive
        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @NotEmpty
        @Valid
        @ArraySchema(schema = @Schema(implementation = OrderItemRequest.class))
        @Schema(description = "주문할 상품 리스트")
        List<OrderItemRequest> orderItems,

        @NotNull
        @Schema(description = "수취인 이름", example = "홍길동")
        String receiverName,

        @NotNull
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$")
        @Schema(description = "수취인 전화번호", example = "010-1234-5678")
        String receiverPhone,

        @NotNull
        @Schema(description = "배송지 주소", example = "서울특별시 강남구 강남대로 123")
        String shippingAddress
) {
        public OrderCommand to() {
                List<OrderItemCommand> orderItemCommands = orderItems.stream()
                        .map(OrderItemRequest::to).toList();
                return new OrderCommand(userId, orderItemCommands, receiverName, receiverPhone, shippingAddress);
        }
}
