package kr.hhplus.be.server.domain.coupon.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.coupon.CouponErrorCode;
import kr.hhplus.be.server.global.exception.DomainException;
import kr.hhplus.be.server.global.model.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;
    @Column(nullable = false)
    private Long discountAmount;
    private Long minOrderAmount;
    private Long maxDiscountAmount;
    @Column(nullable = false)
    private LocalDateTime validFrom;
    @Column(nullable = false)
    private LocalDateTime validUntil;
    @Column(nullable = false)
    private int totalQuantity;

    public void validateCouponPeriod() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(validFrom) || now.isAfter(validUntil)) {
            throw new DomainException(CouponErrorCode.COUPON_INVALID);
        }
    }

    public void validateCouponAvailability(Long actualOrderAmount) {
        if (minOrderAmount != null && actualOrderAmount < minOrderAmount) {
            throw new DomainException(CouponErrorCode.COUPON_NOT_APPLICABLE_TO_PAYMENT);
        }
    }

    public void validateRemainingQuantity() {
        if (totalQuantity <= 0) {
            throw new DomainException(CouponErrorCode.COUPON_STOCK_DEPLETED);
        }
    }

    public void decreaseQuantity() {
        totalQuantity --;
    }

    public Long calculateDiscountAmount(Long actualOrderAmount) {
        Long discountAmount = switch (discountType) {
            case FIXED_AMOUNT -> this.discountAmount;
            case PERCENTAGE -> (this.discountAmount * actualOrderAmount) / 100;
        };

        Long checkMaxAmount = Optional.ofNullable(maxDiscountAmount)
                .map(maxAmount -> Math.min(discountAmount, maxAmount))
                .orElse(discountAmount);

        return Math.min(checkMaxAmount, actualOrderAmount);
    }
}
