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
    private final CouponIssuanceManager couponIssuanceManager;

    @Transactional
    public UseCouponResult useWithLock(Long couponId, Long userId, Long orderAmount) {
        IssuedCoupon coupon = issuedCouponRepository.findByCouponIdAndUserIdWithLock(couponId, userId);
        coupon.use(orderAmount);
        Long discountAmount = coupon.calculateDiscountAmount(orderAmount);
        return new UseCouponResult(discountAmount);
    }

    @Transactional
    public IssueCouponResult issue(IssueCouponCommand command) {
        Coupon coupon = couponRepository.findById(command.couponId())
                .orElseThrow(() -> new BusinessException(CouponErrorCode.COUPON_NOT_FOUND));

        // 쿠폰 수량 차감
        if (couponIssuanceManager.decreaseAndGetQuantity(command.couponId()) < 0) {
            rollbackQuantity(command.couponId());
            throw new BusinessException(CouponErrorCode.COUPON_STOCK_DEPLETED);
        }

        // 쿠폰 발급자 저장
        if (!couponIssuanceManager.saveIssuedUser(command.couponId(), command.userId())) {
            rollbackQuantity(command.couponId());
            throw new BusinessException(CouponErrorCode.COUPON_ALREADY_ISSUED);
        }

        // 발급된 쿠폰 저장
        IssuedCoupon issuedCoupon = issuedCouponRepository.save(new IssuedCoupon(coupon, command.userId()));

        // 쿠폰 잔여 수량 업데이트 (In DB)
        coupon.updateQuantity(couponIssuanceManager.getQuantity(command.couponId()));
        return IssueCouponResult.from(issuedCoupon);
    }

    private void rollbackQuantity(Long couponId) {
        couponIssuanceManager.increaseAndGetQuantity(couponId);
    }

    @Transactional(readOnly = true)
    public void initializeCouponQuantity(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(CouponErrorCode.COUPON_NOT_FOUND));
        couponIssuanceManager.initializeQuantity(couponId, coupon.getTotalQuantity());
    }
}
