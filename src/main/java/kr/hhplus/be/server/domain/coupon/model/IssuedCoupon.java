package kr.hhplus.be.server.domain.coupon.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.coupon.CouponErrorCode;
import kr.hhplus.be.server.global.exception.DomainException;
import kr.hhplus.be.server.global.model.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "issued_coupons")
public class IssuedCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;
    @Column(nullable = false)
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;
    private LocalDateTime used_at;

    public void validateApplicable(Long orderAmount) {
        validateNotUsedCoupon();
        coupon.validateCouponPeriod();
        coupon.validateCouponAvailability(orderAmount);
    }
    
    private void validateNotUsedCoupon() {
        if (status == CouponStatus.USED) {
            throw new DomainException(CouponErrorCode.COUPON_ALREADY_USED);
        }
    }

    public Long calculateDiscountAmount(Long orderAmount) {
        return coupon.calculateDiscountAmount(orderAmount);
    }

    public void useCoupon() {
        status = CouponStatus.USED;
        used_at = LocalDateTime.now();
    }
}
