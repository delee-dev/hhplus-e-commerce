package kr.hhplus.be.server.domain.order.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.model.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;
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

    public Order(Long userId, String receiverName, String receiverPhone, String shippingAddress) {
        this.userId = userId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.shippingAddress = shippingAddress;
        this.status = OrderStatus.PAYMENT_PENDING;
    }

    public void changeOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        this.totalAmount = orderItems.stream().mapToLong(OrderItem::calculateTotalAmount).sum();
    }
}
