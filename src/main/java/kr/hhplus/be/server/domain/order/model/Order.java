package kr.hhplus.be.server.domain.order.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.member.model.User;
import kr.hhplus.be.server.global.model.BaseEntity;

import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;
    private OrderStatus status;
    private long totalAmount;
    private long discountAmount;
    private long finalAmount;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
}
