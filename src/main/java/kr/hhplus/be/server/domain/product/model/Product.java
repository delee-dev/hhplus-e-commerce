package kr.hhplus.be.server.domain.product.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.model.BaseEntity;

@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private long price;
    private long originalPrice;
    @OneToOne(mappedBy = "product")
    private Stock stock;
    private SaleStatus status;
}
