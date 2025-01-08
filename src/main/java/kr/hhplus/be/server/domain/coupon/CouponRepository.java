package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.model.Coupon;

import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findByIdWithLock(Long id);
    Optional<Coupon> findById(Long id);
    Coupon save(Coupon coupon);
}
