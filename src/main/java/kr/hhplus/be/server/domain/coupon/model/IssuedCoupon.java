package kr.hhplus.be.server.domain.coupon.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.member.model.User;
import kr.hhplus.be.server.global.model.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "issued_coupons")
public class IssuedCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private CouponStatus status;
    private LocalDateTime used_at;
}
