package kr.hhplus.be.server.domain.product.dto;

public record DeductStockCommand(
        long productId,
        int quantity
) {
}
