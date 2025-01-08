package kr.hhplus.be.server.infrastructure.product.persistence;

import kr.hhplus.be.server.domain.product.StockRepository;
import kr.hhplus.be.server.domain.product.model.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StockJpaCustomRepository implements StockRepository {
    private final StockJpaRepository stockJpaRepository;

    @Override
    public Stock findByProductIdWithLock(Long productId) {
        return stockJpaRepository.findByProductIdWithLock(productId);
    }

    @Override
    public Stock findByProductId(Long productId) {
        return stockJpaRepository.findByProductId(productId);
    }

    @Override
    public Stock save(Stock stock) {
        return stockJpaRepository.save(stock);
    }
}
