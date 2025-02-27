package kr.hhplus.be.server.api.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.api.coupon.dto.IssueCouponRequest;
import kr.hhplus.be.server.api.coupon.dto.IssueCouponResponse;
import kr.hhplus.be.server.global.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Coupon API", description = "쿠폰 API")
public interface CouponSwaggerApiSpec {
    @Operation(
            summary = "쿠폰 발행",
            description = "쿠폰 ID, 사용자 ID를 입력 받아 쿠폰을 발행합니다.",
            tags = {"Coupon API"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "쿠폰 발급 성공", content = @Content(schema = @Schema(implementation = IssueCouponResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 쿠폰을 발급 받은 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                            "code": 409,
                                            "message": "이미 쿠폰을 발급 받은 사용자 입니다."
                                        }
                                    """
                            ))),
            @ApiResponse(
                    responseCode = "409",
                    description = "사용자가 이미 발급 신청을 완료하여 대기 중",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                            "code": 409,
                                            "message": "이미 발급 대기 중입니다."
                                        }
                                    """
                            ))),
            @ApiResponse(
                    responseCode = "410",
                    description = "쿠폰이 모두 소진된 경우",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                            "code": 410,
                                            "message": "쿠폰이 모두 소진되었습니다."
                                        }
                                    """
                            ))),
    })
    ResponseEntity<IssueCouponResponse> issue(
            @Valid
            @RequestBody(
                    description = "쿠폰 발급 요청",
                    required = true,
                    content = @Content(schema = @Schema(implementation = IssueCouponRequest.class))
            ) IssueCouponRequest request);


    @Operation(
            summary = "쿠폰 수량 초기화",
            description = "Redis에 쿠폰의 수량 정보를 초기화합니다. 쿠폰 엔티티에 설정된 총 수량으로 초기화됩니다.",
            tags = {"Coupon API"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "수량 초기화 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "쿠폰이 모두 소진된 경우",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                            "code": 404,
                                            "message": "존재하지 않는 쿠폰입니다."
                                        }
                                    """
                            ))),
    })
    ResponseEntity<Void> initializeQuantity(@Parameter(description = "쿠폰 ID", example = "1") Long couponId);
}
