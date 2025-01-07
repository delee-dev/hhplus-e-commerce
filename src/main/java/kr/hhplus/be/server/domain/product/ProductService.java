package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.GetProductsQuery;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.global.model.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public PageResponse<ProductResult> getProductsByCategory(GetProductsQuery query) {
        PageResponse<Product> productPage = productRepository.findProductsByCategoryIdAndStatusNot(query.categoryId(), SaleStatus.SUSPENDED, query.page(), query.size(), query.sortColumn(), query.sortDirection());
        return productPage.map(ProductResult::fromEntity);
    }
}
