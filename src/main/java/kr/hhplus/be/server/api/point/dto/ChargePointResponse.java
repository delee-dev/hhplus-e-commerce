package kr.hhplus.be.server.api.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.point.dto.PointResult;

@Schema(description = "포인트 충전 응답 DTO")
public record ChargePointResponse(
        @Schema(description = "사용자 ID", example = "1")
        long userId,
        @Schema(description = "사용자 이름", example = "홍길동")
        String userName,
        @Schema(description = "충전 후 잔액", example = "15000")
        long balance
) {
        public static ChargePointResponse from(PointResult domainDto) {
                return new ChargePointResponse(domainDto.userId(), domainDto.userName(), domainDto.balance());
        }
}
