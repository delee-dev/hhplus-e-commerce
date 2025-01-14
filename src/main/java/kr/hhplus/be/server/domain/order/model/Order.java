package kr.hhplus.be.server.domain.order.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.model.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long userId;
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> orderItems = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    @Column(nullable = false)
    private Long totalAmount;
    @Column(nullable = false)
    private String receiverName;
    @Column(nullable = false)
    private String receiverPhone;
    @Column(nullable = false)
    private String shippingAddress;

    public Order(Long userId, List<OrderItem> orderItems, String receiverName, String receiverPhone, String shippingAddress) {
        this.userId = userId;
        this.totalAmount = 0L;
        orderItems.forEach(this::addOrderItem);
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.shippingAddress = shippingAddress;
        this.status = OrderStatus.PAYMENT_PENDING;
    }

    private void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        this.totalAmount += orderItem.calculateTotalAmount();
        orderItem.assignOrder(this);
    }

    public void completePay() {
        this.status = OrderStatus.PAYMENT_COMPLETED;
    }
}
