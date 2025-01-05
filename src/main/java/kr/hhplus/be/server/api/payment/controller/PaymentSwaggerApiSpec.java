package kr.hhplus.be.server.api.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.api.payment.dto.PayToOrderRequest;
import kr.hhplus.be.server.api.payment.dto.PayToOrderResponse;
import kr.hhplus.be.server.global.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Payment API", description = "결제 API")
public interface PaymentSwaggerApiSpec {
    @Operation(
            summary = "주문 결제",
            description = "사용자 ID, 주문 ID, 적용할 쿠폰 ID를 받아 결제를 처리합니다.",
            tags = {"Payment API"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 완료", content = @Content(schema = @Schema(implementation = PayToOrderResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 결제 완료된 주문",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                       {
                                           "code": 409,
                                           "message": "이미 결제 완료된 주문입니다."
                                       }
                                    """
                            ))),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 결제 취소된 주문",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                       {
                                           "code": 409,
                                           "message": "이미 취소된 주문입니다."
                                       }
                                    """
                            ))),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 사용된 쿠폰",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                       {
                                           "code": 400,
                                           "message": "이미 사용된 쿠폰입니다."
                                       }
                                    """
                            ))),
            @ApiResponse(
                    responseCode = "400",
                    description = "만료된 쿠폰",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                       {
                                           "code": 400,
                                           "message": "만료된 쿠폰입니다."
                                       }
                                    """
                            ))),
            @ApiResponse(
                    responseCode = "400",
                    description = "해당 결제에 적용 불가능한 쿠폰",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                           "code": 400,
                                           "message": "해당 결제에 적용 불가능한 쿠폰 입니다."
                                        }
                                    """
                            ))),
            @ApiResponse(
                    responseCode = "400",
                    description = "포인트 부족",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                       {
                                           "code": 400,
                                           "message": "포인트가 부족합니다."
                                       }
                                    """
                            )))
    })
    ResponseEntity<PayToOrderResponse> payToOrder(
            @RequestBody(
                    description = "주문 결제 요청",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PayToOrderRequest.class))
            ) PayToOrderRequest request);
}
