package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.DeductStockCommand;
import kr.hhplus.be.server.domain.product.dto.GetProductsQuery;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.domain.product.model.Stock;
import kr.hhplus.be.server.global.model.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final int BEST_SELLING_LIMIT = 5;
    private static final int SALES_PERIOD_DAYS = 3;

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public PageResponse<ProductResult> getProductsByCategory(GetProductsQuery query) {
        PageResponse<Product> productPage = productRepository.findProductsByCategoryIdAndStatusNot(query.categoryId(), SaleStatus.SUSPENDED, query.page(), query.size(), query.sortColumn(), query.sortDirection());
        return productPage.map(ProductResult::from);
    }

    @Transactional
    public List<Product> deductStocksWithLock(List<DeductStockCommand> commands) {
        return commands.stream()
                .sorted(Comparator.comparing(DeductStockCommand::productId))
                .map(command -> {
                    Stock stock = stockRepository.findByProductIdWithLock(command.productId());
                    int currentStock = stock.deduct(command.quantity());

                    Product product = stock.getProduct();
                    product.updateSaleStatus(currentStock);

                    stockRepository.save(stock);
                    return productRepository.save(product);
                }).toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "bestSellers", key = "#categoryId")
    public List<ProductResult> getBestSellingProducts(Long categoryId) {
        List<Product> products = productRepository.findBestSellingProductsByCategory(categoryId, SALES_PERIOD_DAYS, BEST_SELLING_LIMIT);
        return products.stream().map(ProductResult::from).toList();
    }

    @Transactional(readOnly = true)
    @CachePut(value = "bestSellers", key = "#categoryId")
    public List<ProductResult> refreshBestSellingProducts(Long categoryId) {
        List<Product> products = productRepository.findBestSellingProductsByCategory(categoryId, SALES_PERIOD_DAYS, BEST_SELLING_LIMIT);
        return products.stream().map(ProductResult::from).toList();
    }

    @Transactional(readOnly = true)
    public List<Long> getAllCategoryIds() {
        return categoryRepository.findAll().stream().map(Category::getId).toList();
    }
}
