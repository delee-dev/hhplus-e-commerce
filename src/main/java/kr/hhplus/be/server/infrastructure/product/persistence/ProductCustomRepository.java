package kr.hhplus.be.server.infrastructure.product.persistence;

import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.global.model.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepository implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;
    private final ProductQueryRepository productQueryRepository;

    @Override
    public PageResponse<Product> findProductsByCategoryIdAndStatusNot(long categoryId, SaleStatus status, int page, int size, String sortColumn, String sortDirection) {
        PageRequest pageRequest = PageRequest
                .of(page, size)
                .withSort(Sort.Direction.fromString(sortDirection), sortColumn);
        Page<Product> productPage = productJpaRepository.findProductsByCategoryIdAndStatusNot(categoryId, status, pageRequest);

        PageResponse.PageInfo pageInfo = new PageResponse.PageInfo(page, size, productPage.getTotalElements(), productPage.getTotalPages());
        return new PageResponse<>(productPage.getContent(), pageInfo);
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public List<Product> findBestSellingProductsByCategory(long categoryId, int period, int limit) {
        return productQueryRepository.findBestSellingProductsByCategory(categoryId, period, limit);
    }
}
