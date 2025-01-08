package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.model.Stock;

public interface StockRepository {
    Stock findByProductIdWithLock(Long productId);
    Stock findByProductId(Long productId);
    Stock save(Stock stock);
}
