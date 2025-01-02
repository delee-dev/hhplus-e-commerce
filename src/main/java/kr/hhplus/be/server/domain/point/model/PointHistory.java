package kr.hhplus.be.server.domain.point.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.model.BaseEntity;

@Entity
@Table(name = "point_histories")
public class PointHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id")
    private Point point;
    private long amount;
    private TransactionType type;
}
