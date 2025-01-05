package kr.hhplus.be.server.api.coupon.dto;

public record IssueCouponRequest(
        long couponId,
        long userId
) {
}
