package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.IssueCouponResult;
import kr.hhplus.be.server.domain.coupon.dto.UseCouponResult;
import kr.hhplus.be.server.domain.coupon.model.Coupon;
import kr.hhplus.be.server.domain.coupon.model.IssuedCoupon;
import kr.hhplus.be.server.global.exception.BusinessException;
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
        coupon.use(orderAmount);
        Long discountAmount = coupon.calculateDiscountAmount(orderAmount);
        return new UseCouponResult(discountAmount);
    }

    @Transactional
    public IssueCouponResult issueWithLock(IssueCouponCommand command) {
        Coupon coupon = couponRepository.findByIdWithLock(command.couponId())
                .orElseThrow(() -> new BusinessException(CouponErrorCode.COUPON_NOT_FOUND));

        if (issuedCouponRepository.existsByCouponIdAndUserId(command.couponId(), command.userId())) {
            throw new BusinessException(CouponErrorCode.COUPON_ALREADY_ISSUED);
        }

        IssuedCoupon issuedCoupon = coupon.issue(command.userId());
        issuedCouponRepository.save(issuedCoupon);

        return IssueCouponResult.from(issuedCoupon);
    }
}
