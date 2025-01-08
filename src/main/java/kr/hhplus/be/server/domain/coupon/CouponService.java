package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.IssueCouponResult;
import kr.hhplus.be.server.domain.coupon.dto.UseCouponResult;
import kr.hhplus.be.server.domain.coupon.model.Coupon;
import kr.hhplus.be.server.domain.coupon.model.IssuedCoupon;
import kr.hhplus.be.server.global.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final IssuedCouponRepository issuedCouponRepository;
    private final CouponRepository couponRepository;

    public UseCouponResult useWithLock(Long couponId, Long userId, Long orderAmount) {
        IssuedCoupon coupon = issuedCouponRepository.findByCouponIdAndUserIdWithLock(couponId, userId);
        coupon.validateApplicable(orderAmount);
        coupon.useCoupon();
        issuedCouponRepository.save(coupon);
        return new UseCouponResult(coupon.calculateDiscountAmount(orderAmount));
    }

    @Transactional
    public IssueCouponResult issueWithLock(IssueCouponCommand command) {
        Coupon coupon = couponRepository.findByIdWithLock(command.couponId())
                .orElseThrow(() -> new DomainException(CouponErrorCode.COUPON_NOT_FOUND));
        coupon.validateRemainingQuantity();

        if (issuedCouponRepository.existsByCouponIdAndUserId(command.couponId(), command.userId())) {
            throw new DomainException(CouponErrorCode.COUPON_ALREADY_ISSUED);
        }

        coupon.decreaseQuantity();
        IssuedCoupon issuedCoupon = new IssuedCoupon(coupon, command.userId());

        couponRepository.save(coupon);
        issuedCouponRepository.save(issuedCoupon);

        return IssueCouponResult.fromEntity(issuedCoupon);
    }
}
