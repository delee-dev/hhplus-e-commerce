package kr.hhplus.be.server.api.point.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.api.point.dto.ChargePointRequest;
import kr.hhplus.be.server.api.point.dto.ChargePointResponse;
import kr.hhplus.be.server.api.point.dto.GetPointResponse;
import kr.hhplus.be.server.global.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Point API", description = "포인트 API")
public interface PointSwaggerApiSpec {
    @Operation(
            summary = "포인트 조회",
            description = "사용자 ID를 입력 받아 포인트를 조회합니다.",
            tags = {"Point API"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포인트 조회 성공", content = @Content(schema = @Schema(implementation = GetPointResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                            "code": 404,
                                            "message": "회원 정보를 찾을 수 없습니다."
                                        }
                                    """
                            )))
    })
    @Parameter(name = "userId", description = "사용자 ID", example = "1")
    ResponseEntity<GetPointResponse> getPoint(long userId);

    @Operation(
            summary = "포인트 충전",
            description = "사용자 ID, 충전 금액을 입력 받아 포인트를 충전합니다.",
            tags = {"Point API"}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "충전 성공", content = @Content(schema = @Schema(implementation = ChargePointResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                            "code": 404,
                                            "message": "회원 정보를 찾을 수 없습니다."
                                        }
                                    """
                            ))),
            @ApiResponse(
                    responseCode = "400",
                    description = "1회 최소 충전 금액 미달",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                            "code": 400,
                                            "message": "1회 최소 충전 금액을 미달했습니다."
                                        }
                                    """
                            ))),
            @ApiResponse(
                    responseCode = "400",
                    description = "1회 최대 충전 금액 초과",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                            "code": 400,
                                            "message": "1회 최대 충전 한도를 초과했습니다."
                                        }
                                    """
                            ))),
            @ApiResponse(
                    responseCode = "400",
                    description = "포인트 보유 한도 초과",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                            "code": 400,
                                            "message": "포인트 보유 한도를 초과하였습니다."
                                        }
                                    """
                            ))),
            @ApiResponse(
                    responseCode = "409",
                    description = "다중 수정 요청으로 인한 충돌",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                        {
                                            "code": 409,
                                            "message": "데이터가 이미 변경되었습니다. 최신 데이터를 확인해주세요."
                                        }
                                    """
                            )))
    })
    ResponseEntity<ChargePointResponse> charge(
            @RequestBody(
                    description = "포인트 충전 요청",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChargePointRequest.class))
            ) ChargePointRequest request);

}
