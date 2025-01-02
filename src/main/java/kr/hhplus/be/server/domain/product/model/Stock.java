package kr.hhplus.be.server.domain.product.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.model.BaseEntity;

@Entity
@Table(name = "stocks")
public class Stock extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    private int quantity;
}
