package kr.hhplus.be.server.infrastructure.coupon.persistence;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.model.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Component;

@Component
public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCoupon, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    IssuedCoupon findByUserIdAndCoupon_Id(Long userId, Long couponId);
}
