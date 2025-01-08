package kr.hhplus.be.server.infrastructure.product.persistence;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface StockJpaRepository extends JpaRepository<Stock, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.product.id = :productId")
    Stock findByProductIdWithLock(@Param("productId") Long productId);
    Stock findByProductId(Long productId);
}
