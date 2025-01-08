package kr.hhplus.be.server.domain.payment.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.model.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long orderId;
    @Column(nullable = false)
    private Long totalAmount;
    @Column(nullable = false)
    @ColumnDefault("0")
    private Long discountAmount;
    @Column(nullable = false)
    private Long finalAmount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    public Payment(Long orderId, Long totalAmount) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.discountAmount = 0L;
        this.finalAmount = totalAmount - discountAmount;
        this.status = PaymentStatus.PENDING;
    }
}
