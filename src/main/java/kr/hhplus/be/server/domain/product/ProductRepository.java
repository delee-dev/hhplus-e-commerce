package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.global.model.PageResponse;

public interface ProductRepository {
    PageResponse<Product> findProductsByCategoryIdAndStatusNot(long categoryId, SaleStatus status, int page, int size, String sortColumn, String sortDirection);
}
