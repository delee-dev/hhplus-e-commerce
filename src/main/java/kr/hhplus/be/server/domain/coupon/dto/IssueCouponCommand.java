package kr.hhplus.be.server.domain.coupon.dto;

public record IssueCouponCommand(
        long couponId,
        long userId
) {
}
