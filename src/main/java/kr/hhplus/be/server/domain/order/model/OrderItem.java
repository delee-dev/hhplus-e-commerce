package kr.hhplus.be.server.domain.order.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.model.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @Column(nullable = false)
    private Long productId;
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false)
    private Long price;
    @Column(nullable = false)
    private int quantity;

    public OrderItem(Order order, Long productId, String productName, Long price, int quantity) {
        this.order = order;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    protected long calculateTotalAmount() {
        return price * quantity;
    }
}
