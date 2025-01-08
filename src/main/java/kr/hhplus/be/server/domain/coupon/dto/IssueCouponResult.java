package kr.hhplus.be.server.domain.coupon.dto;

import kr.hhplus.be.server.domain.coupon.model.IssuedCoupon;

public record IssueCouponResult(
        boolean success,
        Long issuedCouponId
) {
    public static IssueCouponResult fromEntity(IssuedCoupon issuedCoupon) {
        return new IssueCouponResult(true, issuedCoupon.getId());
    }
}
