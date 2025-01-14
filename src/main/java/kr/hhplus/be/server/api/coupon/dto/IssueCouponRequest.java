package kr.hhplus.be.server.api.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.domain.coupon.dto.IssueCouponCommand;

@Schema(description = "쿠폰 발급 요청 DTO")
public record IssueCouponRequest(
        @NotNull
        @Positive
        @Schema(description = "쿠폰 ID", example = "1")
        Long couponId,

        @NotNull
        @Positive
        @Schema(description = "사용자 ID", example = "1")
        Long userId
) {
        public IssueCouponCommand to() {
                return new IssueCouponCommand(couponId, userId);
        }
}
