package kr.hhplus.be.server.api.order.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "주문 생성 요청 DTO")
public record OrderRequest(
        @Schema(description = "사용자 ID", example = "1")
        long userId,
        @ArraySchema(schema = @Schema(implementation = OrderItemRequest.class))
        @Schema(description = "주문할 상품 리스트")
        List<OrderItemRequest> orderItems,
        @Schema(description = "수취인 이름", example = "홍길동")
        String receiverName,
        @Schema(description = "수취인 전화번호", example = "010-1234-5678")
        String receiverPhone,
        @Schema(description = "배송지 주소", example = "서울특별시 강남구 강남대로 123")
        String shippingAddress
) {
}
