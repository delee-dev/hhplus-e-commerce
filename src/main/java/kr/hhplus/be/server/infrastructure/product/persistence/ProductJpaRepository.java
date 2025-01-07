package kr.hhplus.be.server.infrastructure.product.persistence;

import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    Page<Product> findProductsByCategoryIdAndStatusNot(long categoryId, SaleStatus status, Pageable pageable);
}
