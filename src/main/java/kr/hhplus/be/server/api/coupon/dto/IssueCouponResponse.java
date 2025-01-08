package kr.hhplus.be.server.api.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.dto.IssueCouponResult;

@Schema(description = "쿠폰 발급 응답 DTO")
public record IssueCouponResponse(
        @Schema(description = "쿠폰 발급 성공 여부", example = "true")
        boolean success,
        @Schema(description = "발급된 쿠폰 ID", example = "1")
        Long issuedCouponId
) {
        public static IssueCouponResponse fromApp(IssueCouponResult issueCouponResult) {
                return new IssueCouponResponse(issueCouponResult.success(), issueCouponResult.issuedCouponId());
        }
}
