package kr.hhplus.be.server.api.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.dto.IssueCouponCommand;

@Schema(description = "쿠폰 발급 요청 DTO")
public record IssueCouponRequest(
        @Schema(description = "쿠폰 ID", example = "1")
        long couponId,
        @Schema(description = "사용자 ID", example = "1")
        long userId
) {
        public IssueCouponCommand toApp() {
                return new IssueCouponCommand(couponId, userId);
        }
}
