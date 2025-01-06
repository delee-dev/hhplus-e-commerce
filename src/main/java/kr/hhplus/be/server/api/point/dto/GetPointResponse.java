package kr.hhplus.be.server.api.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 조회 응답 DTO")
public record GetPointResponse(
        @Schema(description = "사용자 ID", example = "1")
        long userId,
        @Schema(description = "사용자 이름", example = "홍길동")
        String userName,
        @Schema(description = "포인트 잔액", example = "10000")
        long balance
) {
}
