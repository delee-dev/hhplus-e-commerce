package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.DeductStockCommand;
import kr.hhplus.be.server.domain.product.dto.GetProductsQuery;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.domain.product.model.Stock;
import kr.hhplus.be.server.global.model.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Transactional(readOnly = true)
    public PageResponse<ProductResult> getProductsByCategory(GetProductsQuery query) {
        PageResponse<Product> productPage = productRepository.findProductsByCategoryIdAndStatusNot(query.categoryId(), SaleStatus.SUSPENDED, query.page(), query.size(), query.sortColumn(), query.sortDirection());
        return productPage.map(ProductResult::fromEntity);
    }

    public List<Product> deductStocksWithLock(List<DeductStockCommand> commands) {
        return commands.stream()
                .map(command -> {
                    Stock stock = stockRepository.findByProductIdWithLock(command.productId());
                    int currentStock = stock.deduct(command.quantity());

                    Product product = stock.getProduct();
                    product.updateSaleStatus(currentStock);

                    stockRepository.save(stock);
                    return productRepository.save(product);
                }).toList();
    }
}
