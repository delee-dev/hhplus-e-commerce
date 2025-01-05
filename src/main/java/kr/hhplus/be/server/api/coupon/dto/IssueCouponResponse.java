package kr.hhplus.be.server.api.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "쿠폰 발급 응답 DTO")
public record IssueCouponResponse(
        @Schema(description = "쿠폰 발급 성공 여부", example = "true")
        boolean success
) {
}
