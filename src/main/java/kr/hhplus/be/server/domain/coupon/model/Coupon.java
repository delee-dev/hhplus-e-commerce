package kr.hhplus.be.server.domain.coupon.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.model.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private DiscountType discountType;
    private long discountAmount;
    private long minOrderAmount;
    private long maxDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private int totalQuantity;
}
