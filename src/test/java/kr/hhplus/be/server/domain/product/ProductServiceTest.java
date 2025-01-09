package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.DeductStockCommand;
import kr.hhplus.be.server.domain.product.dto.GetProductsQuery;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.domain.product.model.Stock;
import kr.hhplus.be.server.fixture.TestDataFactory;
import kr.hhplus.be.server.global.exception.DomainException;
import kr.hhplus.be.server.global.model.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockRepository stockRepository;

    @Nested
    @DisplayName("상품 리스트 조회")
    class GetProductsTest {
        @Test
        void 카테고리별_상품_조회_시_판매_중단_상품은_조회되지_않는다() {
            // given
            Category category = TestDataFactory.createCategory();
            List<Product> products = TestDataFactory.createProducts();

            GetProductsQuery query = new GetProductsQuery(category.getId(), 0, 10, "createdAt", "asc");
            PageResponse<Product> productPage = new PageResponse<>(products, new PageResponse.PageInfo(query.page(), query.page(), products.size(), 0));

            when(productRepository.findProductsByCategoryIdAndStatusNot(query.categoryId(), SaleStatus.SUSPENDED, query.page(), query.size(), query.sortColumn(), query.sortDirection()))
                    .thenReturn(productPage);

            // when
            PageResponse<ProductResult> result = productService.getProductsByCategory(query);

            // then
            assertThat(result.getContent())
                    .extracting(ProductResult::status)
                    .doesNotContain(SaleStatus.SUSPENDED);
        }

        @Test
        void 상품이_등록되지_않은_카테고리를_검색하면_빈_리스트를_반환한다() {
            // given
            Category emptyCategory = TestDataFactory.createEmptyCategory();
            List<Product> emptyProducts = List.of();

            GetProductsQuery query = new GetProductsQuery(emptyCategory.getId(), 0, 10, "createdAt", "asc");
            PageResponse<Product> productPage = new PageResponse<>(emptyProducts, new PageResponse.PageInfo(query.page(), query.page(), emptyProducts.size(), 0));

            when(productRepository.findProductsByCategoryIdAndStatusNot(query.categoryId(), SaleStatus.SUSPENDED, query.page(), query.size(), query.sortColumn(), query.sortDirection()))
                    .thenReturn(productPage);

            // when
            PageResponse<ProductResult> result = productService.getProductsByCategory(query);

            // then
            assertThat(result.getContent()).hasSize(0);
        }
    }

    @Nested
    @DisplayName("상품 재고 차감")
    class DeductStockTest {
        @Test
        void 차감_후의_재고는_차감_전_재고에서_주문량을_뺀_값과_같다() {
            // given
            Stock stock = TestDataFactory.createStocks().get(0);
            Product product = stock.getProduct();
            int orderQuantity = 2;
            int expectedQuantity = stock.getQuantity() - orderQuantity;

            List<DeductStockCommand> commands = List.of(new DeductStockCommand(product.getId(), orderQuantity));

            when(stockRepository.findByProductIdWithLock(product.getId())).thenReturn(stock);

            // when
            productService.deductStocksWithLock(commands);

            // then
            assertThat(stock.getQuantity()).isEqualTo(expectedQuantity);
        }

        @Test
        void 재고가_부족하면_재고_차감에_실패한다() {
            // given
            Stock stock = TestDataFactory.createStocks().get(0);
            Product product = stock.getProduct();
            int orderQuantity = TestDataFactory.StockConstants.EXCEED_CURRENT_STOCK_QUANTITY;

            List<DeductStockCommand> commands = List.of(new DeductStockCommand(product.getId(), orderQuantity));

            when(stockRepository.findByProductIdWithLock(product.getId())).thenReturn(stock);

            // when & then
            assertThatThrownBy(() -> productService.deductStocksWithLock(commands))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(ProductErrorCode.PRODUCT_OUT_OF_STOCK.getMessage());
        }

        @Test
        @DisplayName("재고가 0이 되면 상품 판매상태가 TEMPORARILY_OUT으로 변경된다")
        void 재고가_0이_되면_상품_판매_상태가_TEMPORARILY_OUT으로_변경된다() {
            // given
            Stock stock = TestDataFactory.createStocks().get(0);
            Product product = stock.getProduct();
            int orderQuantity = TestDataFactory.StockConstants.CURRENT_STOCK_QUANTITY;

            List<DeductStockCommand> commands = List.of(new DeductStockCommand(product.getId(), orderQuantity));

            when(stockRepository.findByProductIdWithLock(product.getId())).thenReturn(stock);

            // when
            productService.deductStocksWithLock(commands);

            // then
            assertThat(stock.getQuantity()).isZero();
            assertThat(product.getStatus()).isEqualTo(SaleStatus.TEMPORARILY_OUT);
        }
    }
}
