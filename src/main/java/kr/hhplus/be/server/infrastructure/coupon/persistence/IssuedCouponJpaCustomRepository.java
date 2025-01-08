package kr.hhplus.be.server.infrastructure.coupon.persistence;

import kr.hhplus.be.server.domain.coupon.IssuedCouponRepository;
import kr.hhplus.be.server.domain.coupon.model.IssuedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IssuedCouponJpaCustomRepository implements IssuedCouponRepository {
    private final IssuedCouponJpaRepository issuedCouponJpaRepository;

    @Override
    public IssuedCoupon findByCouponIdAndUserIdWithLock(Long couponId, Long userId) {
        return issuedCouponJpaRepository.findByUserIdAndCoupon_Id(userId, couponId);
    }

    @Override
    public IssuedCoupon save(IssuedCoupon coupon) {
        return issuedCouponJpaRepository.save(coupon);
    }
}
