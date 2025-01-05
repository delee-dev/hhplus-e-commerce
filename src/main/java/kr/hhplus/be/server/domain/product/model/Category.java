package kr.hhplus.be.server.domain.product.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.model.BaseEntity;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
