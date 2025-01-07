package kr.hhplus.be.server.domain.point.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.point.PointErrorCode;
import kr.hhplus.be.server.global.exception.DomainException;
import kr.hhplus.be.server.global.model.BaseEntity;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "points")
@Getter
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ColumnDefault("0")
    private long balance;
    @Column(nullable = false)
    private Long userId;
    @Version
    @ColumnDefault("0")
    private Long version;

    @Transient
    private final long MINIMUM_CHARGE_POINT = 1_000;
    @Transient
    private final long MAXIMUM_CHARGE_POINT = 1_000_000;
    @Transient
    private final long MAXIMUM_BALANCE = 10_000_000;

    public void charge(long amount) {
        assertMinimumChargeAmount(amount);
        assertMaximumChargeAmount(amount);
        long pointAfterCharge = balance + amount;
        assertBalanceLimit(pointAfterCharge);
        balance = pointAfterCharge;
    }

    private void assertMinimumChargeAmount(long amount) {
        if (amount < MINIMUM_CHARGE_POINT) {
            throw new DomainException(PointErrorCode.POINT_CHARGE_BELOW_MINIMUM);
        }
    }

    private void assertMaximumChargeAmount(long amount) {
        if (amount > MAXIMUM_CHARGE_POINT) {
            throw new DomainException(PointErrorCode.POINT_CHARGE_EXCEEDS_MAXIMUM);
        }
    }

    private void assertBalanceLimit(long amount) {
        if (amount > MAXIMUM_BALANCE) {
            throw new DomainException(PointErrorCode.POINT_BALANCE_EXCEEDS_LIMIT);
        }
    }
}
