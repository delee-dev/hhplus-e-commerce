package kr.hhplus.be.server.domain.coupon.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.coupon.CouponErrorCode;
import kr.hhplus.be.server.global.exception.BusinessException;
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

    public void updateQuantity(int quantity) {
        this.totalQuantity = quantity;
    }

    public void validateUsage(Long orderAmount) {
        validatePeriod();
        validateMinimumOrderAmount(orderAmount);
    }

    private void validatePeriod() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(validFrom) || now.isAfter(validUntil)) {
            throw new BusinessException(CouponErrorCode.COUPON_INVALID);
        }
    }

    private void validateMinimumOrderAmount(Long orderAmount) {
        if (minOrderAmount != null && orderAmount < minOrderAmount) {
            throw new BusinessException(CouponErrorCode.COUPON_NOT_APPLICABLE_TO_PAYMENT);
        }
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
