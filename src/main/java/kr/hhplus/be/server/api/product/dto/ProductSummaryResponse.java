package kr.hhplus.be.server.api.product.dto;

import kr.hhplus.be.server.domain.product.model.SaleStatus;

public record ProductSummaryResponse(
        long id,
        String name,
        String description,
        String category,
        long price,
        long originalPrice,
        int stock,
        SaleStatus status
) {
}
