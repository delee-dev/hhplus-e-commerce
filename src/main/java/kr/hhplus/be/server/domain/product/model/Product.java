package kr.hhplus.be.server.domain.product.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.model.BaseEntity;
import lombok.Getter;

@Entity
@Table(name = "products")
@Getter
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(nullable = false)
    private long price;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus status;

    public void updateSaleStatus(int currentStock) {
        if (currentStock == 0) {
            status = SaleStatus.TEMPORARILY_OUT;
        }
    }

    public String getCategoryName() {
        return category.getName();
    }
}
