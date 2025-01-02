package kr.hhplus.be.server.domain.payment.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.global.model.BaseEntity;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
    private long amount;
    private PaymentStatus status;
}
