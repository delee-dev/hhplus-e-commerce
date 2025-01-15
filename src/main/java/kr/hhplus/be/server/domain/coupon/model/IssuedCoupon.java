package kr.hhplus.be.server.domain.coupon.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.coupon.CouponErrorCode;
import kr.hhplus.be.server.global.exception.BusinessException;
import kr.hhplus.be.server.global.model.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "issued_coupons",
        uniqueConstraints = {
                @UniqueConstraint( // TODO: 위반 시, DomainException 으로 변환할 수 있는 방법
                        name = "uk_issued_coupon_coupon_id_user_id",
                        columnNames = {"coupon_id", "user_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    public IssuedCoupon(Coupon coupon, Long userId) {
        this.coupon = coupon;
        this.userId = userId;
        this.status = CouponStatus.AVAILABLE;
    }

    public void use(Long orderAmount) {
        validateUsage(orderAmount);
        status = CouponStatus.USED;
        used_at = LocalDateTime.now();
    }

    private void validateUsage(Long orderAmount) {
        validateNotUsed();
        coupon.validateUsage(orderAmount);
    }
    
    private void validateNotUsed() {
        if (status == CouponStatus.USED) {
            throw new BusinessException(CouponErrorCode.COUPON_ALREADY_USED);
        }
    }

    public Long calculateDiscountAmount(Long orderAmount) {
        return coupon.calculateDiscountAmount(orderAmount);
    }
}
