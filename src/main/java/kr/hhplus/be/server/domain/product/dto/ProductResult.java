package kr.hhplus.be.server.domain.product.dto;

import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;

public record ProductResult(
        long id,
        String name,
        String description,
        String category,
        long price,
        SaleStatus status
) {
    public static ProductResult fromEntity(Product product) {
        return new ProductResult(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategoryName(),
                product.getPrice(),
                product.getStatus()
        );
    }
}
