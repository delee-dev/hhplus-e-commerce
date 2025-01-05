package kr.hhplus.be.server.domain.statistics.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.global.model.BaseEntity;

@Entity
@Table(name = "best_sellers")
public class BestSeller extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private int sales_count;
    private int ranking;
}
