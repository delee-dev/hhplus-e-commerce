package kr.hhplus.be.server.domain.point.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.model.BaseEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "point_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;
    @Column(nullable = false)
    private long amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    public PointHistory(Point point, long amount, TransactionType type) {
        this.point = point;
        this.amount = amount;
        this.type = type;
    }
}
