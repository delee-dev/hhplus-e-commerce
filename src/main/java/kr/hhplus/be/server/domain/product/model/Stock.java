package kr.hhplus.be.server.domain.product.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.ProductErrorCode;
import kr.hhplus.be.server.global.exception.DomainException;
import kr.hhplus.be.server.global.model.BaseEntity;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "stocks")
@Getter
public class Stock extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    private Product product;
    @Column(nullable = false)
    @ColumnDefault("0")
    private int quantity;

    public int deduct(int amount) {
        if (quantity - amount < 0) {
            throw new DomainException(ProductErrorCode.PRODUCT_OUT_OF_STOCK);
        }
        quantity -= amount;
        return quantity;
    }
}
