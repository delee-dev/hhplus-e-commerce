package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.model.IssuedCoupon;

public interface IssuedCouponRepository {
    IssuedCoupon findByCouponIdAndUserIdWithLock(Long couponId, Long userId);
    IssuedCoupon save(IssuedCoupon coupon);
}
