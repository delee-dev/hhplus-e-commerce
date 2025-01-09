package kr.hhplus.be.server.domain.statistics.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.global.model.BaseEntity;

import java.time.LocalDate;

//@Entity
@Table(name = "sales_statistics")
public class SalesStatistics extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    private int salesCount;
    private LocalDate date;
}
