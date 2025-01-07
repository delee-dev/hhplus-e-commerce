package kr.hhplus.be.server.domain.product.dto;

public record GetProductsQuery(
        long categoryId,
        int page,
        int size,
        String sortColumn,
        String sortDirection
) {
}
