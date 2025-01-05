package kr.hhplus.be.server.api.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "쿠폰 발급 요청 DTO")
public record IssueCouponRequest(
        @Schema(description = "쿠폰 ID", example = "1")
        long couponId,
        @Schema(description = "사용자 ID", example = "1")
        long userId
) {
}
