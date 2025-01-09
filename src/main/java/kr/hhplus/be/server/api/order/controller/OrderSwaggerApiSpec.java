package kr.hhplus.be.server.api.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.api.order.dto.OrderRequest;
import kr.hhplus.be.server.api.order.dto.OrderResponse;
import kr.hhplus.be.server.global.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Order API", description = "주문 API")
public interface OrderSwaggerApiSpec {
    @Operation(
            summary = "상품 주문",
            description = "사용자 ID, 상품 리스트, 배송 정보를 입력 받아 주문을 생성합니다.",
            tags = {"Order API"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 생성 완료", content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "주문한 상품의 재고 부족",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                            "code": 400,
                                            "message": "상품의 재고가 부족합니다."
                                        }
                                    """
                            )))
    })
    ResponseEntity<OrderResponse> order(
            @Valid
            @RequestBody(
                    description = "주문 생성 요청",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderRequest.class))
            ) OrderRequest request);
}
