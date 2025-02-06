package kr.hhplus.be.server.domain.product.integration;

import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.product.ProductErrorCode;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.dto.DeductStockCommand;
import kr.hhplus.be.server.domain.product.dto.GetProductsQuery;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.domain.product.model.Stock;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.global.exception.BusinessException;
import kr.hhplus.be.server.global.model.PageResponse;
import kr.hhplus.be.server.infrastructure.order.persistence.OrderJpaRepository;
import kr.hhplus.be.server.infrastructure.payment.persistence.PaymentJpaRepository;
import kr.hhplus.be.server.infrastructure.product.persistence.CategoryJapRepository;
import kr.hhplus.be.server.infrastructure.product.persistence.ProductJpaRepository;
import kr.hhplus.be.server.infrastructure.product.persistence.StockJpaRepository;
import kr.hhplus.be.server.infrastructure.user.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static kr.hhplus.be.server.fixture.integration.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Sql("/clear.sql")
public class ProductServiceIntegrationTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryJapRepository categoryJapRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private StockJpaRepository stockJpaRepository;
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    @Autowired
    private PaymentJpaRepository paymentJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private CacheManager cacheManager;
    @MockitoSpyBean
    private ProductRepository productRepository;


    @Nested
    @DisplayName("상품 리스트 조회")
    class GetProducts {
        @BeforeEach
        void setUp() {
            Category category = category();
            categoryJapRepository.saveAndFlush(category);

            List<Product> products = products(category);
            productJpaRepository.saveAllAndFlush(products);
        }

        @Test
        void 상품_리스트_조회_시_판매_중단_상품은_조회되지_않는다() {
            // given
            GetProductsQuery query = new GetProductsQuery(1L, 0, 10, "createdAt", "asc");

            // when
            PageResponse<ProductResult> result = productService.getProductsByCategory(query);

            // then
            assertThat(result.getContent())
                    .extracting(ProductResult::status)
                    .doesNotContain(SaleStatus.SUSPENDED);
        }

        @Test
        void 상품_리스트_조회_시_페이징_처리된다() {
            // given
            GetProductsQuery query = new GetProductsQuery(1L, 0, 5, "createdAt", "asc");

            // when
            PageResponse<ProductResult> result = productService.getProductsByCategory(query);

            // then
            assertThat(result.getPageInfo().getPage()).isEqualTo(query.page());
            assertThat(result.getPageInfo().getSize()).isEqualTo(query.size());
            assertThat(result.getContent()).hasSize(query.size());
        }

        @Test
        void 상품_리스트_조회_시_정렬_조건에_따라_정렬된다() {
            // given
            GetProductsQuery query = new GetProductsQuery(1L, 0, 10, "price", "desc");

            // when
            PageResponse<ProductResult> result = productService.getProductsByCategory(query);

            // then
            assertThat(result.getContent())
                    .extracting(ProductResult::price)
                    .isSortedAccordingTo(Comparator.reverseOrder());
        }

        @Test
        void 한글_데이터를_기준으로_정렬해도_정상적으로_정렬된다() {
            // given
            GetProductsQuery query = new GetProductsQuery(1L, 0, 10, "name", "asc");

            // when
            PageResponse<ProductResult> result = productService.getProductsByCategory(query);

            // then
            List<ProductResult> content = result.getContent();
            assertThat(content)
                    .isSortedAccordingTo(Comparator.comparing(ProductResult::name));
        }
    }

    @Nested
    @DisplayName("상위 판매 상품 리스트 조회")
    class GetBestSellingProducts {
        @BeforeEach
        void setUp() {
            User user = user();
            userJpaRepository.saveAndFlush(user);

            Category category = category();
            categoryJapRepository.saveAndFlush(category);

            List<Product> products = products(category);
            productJpaRepository.saveAllAndFlush(products);

            List<Order> orders = orders(user, products);
            orderJpaRepository.saveAllAndFlush(orders);

            List<Payment> payments = payments(orders);
            paymentJpaRepository.saveAllAndFlush(payments);

            Objects.requireNonNull(cacheManager.getCache("bestSellers"))
                    .evictIfPresent(category.getId());
        }

        @Test
        void 상위_판매_상품_리스트_조회() {
            // given
            long categoryId = 1;

            // when
            List<ProductResult> result = productService.getBestSellingProducts(categoryId);

            // then
            assertThat(result).hasSize(3); // 3일치만 집계
            assertThat(result.get(0))
                    .extracting("id", "name")
                    .contains(3L, "스마트폰");

            assertThat(result.get(1))
                    .extracting("id", "name")
                    .contains(2L, "태블릿PC");

            assertThat(result.get(2))
                    .extracting("id", "name")
                    .contains(1L, "무선이어폰");
        }
    }

    @Nested
    @DisplayName("상위 판매 상품 캐시")
    class BestSellingProductsCache {
        @BeforeEach
        void setUp() {
            User user = user();
            userJpaRepository.saveAndFlush(user);

            Category category = category();
            categoryJapRepository.saveAndFlush(category);

            List<Product> products = products(category);
            productJpaRepository.saveAllAndFlush(products);

            List<Order> orders = orders(user, products);
            orderJpaRepository.saveAllAndFlush(orders);

            List<Payment> payments = payments(orders);
            paymentJpaRepository.saveAllAndFlush(payments);

            Objects.requireNonNull(cacheManager.getCache("bestSellers"))
                    .evictIfPresent(category.getId());
        }

        @Test
        void 상위_판매_상품_조회_시_DB_접근은_한_번만_일어난다() {
            // given
            long categoryId = 1;

            // when
            productService.getBestSellingProducts(categoryId);
            productService.getBestSellingProducts(categoryId);

            // then
            verify(productRepository, times(1))
                    .findBestSellingProductsByCategory(eq(categoryId), anyInt(), anyInt());
        }

        @Test
        void 상위_판매_상품_갱신_시_조회_결과가_캐시에_저장된다() {
            // given
            long categoryId = 1;

            // when
            List<ProductResult> results = productService.refreshBestSellingProducts(categoryId);

            // then
            Cache cache = cacheManager.getCache("bestSellers");
            List<ProductResult> cachedResults = cache.get(categoryId, List.class);

            assertThat(cachedResults)
                    .isNotNull()
                    .hasSameElementsAs(results);
        }
    }

    @Nested
    @DisplayName("재고 차감 기능")
    class DeductStockTest {
        @BeforeEach
        void setUp() {
            Category category = category();
            categoryJapRepository.saveAndFlush(category);

            Product product = product(category);
            productJpaRepository.saveAndFlush(product);

            Stock stock = stock(product, 10);
            stockJpaRepository.saveAndFlush(stock);
        }

        @Test
        void 재고_차감_요청시_재고_수량이_정상적으로_감소한다() {
            // given
            Long productId = 1L;
            int quantity = 5;

            int beforeQuantity = stockJpaRepository.findByProductId(productId).getQuantity();
            int expectedQuantity = beforeQuantity - quantity;

            List<DeductStockCommand> commands = List.of(new DeductStockCommand(productId, quantity));

            // when
            productService.deductStocksWithLock(commands);

            // then
            int actualQuantity = stockJpaRepository.findByProductId(productId).getQuantity();
            assertThat(actualQuantity).isEqualTo(expectedQuantity);
        }

        @Test
        void 재고가_0이_되면_상품의_상태가_일시_품절로_바뀐다() {
            // given
            Long productId = 1L;
            int quantity = stockJpaRepository.findByProductId(productId).getQuantity();

            List<DeductStockCommand> commands = List.of(new DeductStockCommand(productId, quantity));

            // when
            productService.deductStocksWithLock(commands);

            // then
            SaleStatus actualStatus = productJpaRepository.findById(productId).get().getStatus();
            assertThat(actualStatus).isEqualTo(SaleStatus.TEMPORARILY_OUT);
        }
    }

    @Nested
    @DisplayName("재고 차감 동시성 테스트")
    class DeductStockConcurrencyTest {
        @BeforeEach
        void setUp() {
            Category category = category();
            categoryJapRepository.saveAndFlush(category);

            Product product = product(category);
            productJpaRepository.saveAndFlush(product);

            Stock stock = stock(product, 10);
            stockJpaRepository.saveAndFlush(stock);
        }

        @Test
        void 동시에_동일한_상품의_재고_차감을_요청하는_경우_한_번에_하나씩_처리된다() throws InterruptedException {
            // given
            long productId = 1L;
            int quantity = 1;
            int requestCount = 2;

            int beforeQuantity = stockJpaRepository.findByProductId(productId).getQuantity();
            int expectedQuantity = beforeQuantity - quantity * requestCount;

            // when
            ExecutorService executor = Executors.newFixedThreadPool(requestCount);
            CountDownLatch latch = new CountDownLatch(requestCount);

            List<DeductStockCommand> commands = List.of(new DeductStockCommand(productId, quantity));

            for (int i = 0; i < requestCount; i++) {
                executor.execute(() -> {
                    productService.deductStocksWithLock(commands);
                    latch.countDown();
                });
            }

            latch.await();
            executor.shutdown();

            // then
            int actualQuantity = stockJpaRepository.findByProductId(productId).getQuantity();
            assertThat(actualQuantity).isEqualTo(expectedQuantity);
        }

        @Test
        void 동시에_동일한_상품의_재고_차감을_요청하는_경우_재고가_부족하면_예외가_발생한다() throws InterruptedException {
            // given
            long productId = 1L;
            int quantity = 7;

            int beforeQuantity = stockJpaRepository.findByProductId(productId).getQuantity();
            int expectedQuantity = beforeQuantity - quantity;

            // when & then
            int requestCount = 2;
            ExecutorService executor = Executors.newFixedThreadPool(requestCount);
            CountDownLatch latch = new CountDownLatch(requestCount);
            AtomicInteger successCount = new AtomicInteger(0);

            List<DeductStockCommand> commands = List.of(new DeductStockCommand(productId, quantity));

            for (int i = 0; i < requestCount; i++) {
                executor.execute(() -> {
                    try {
                        productService.deductStocksWithLock(commands);
                        successCount.incrementAndGet();
                    } catch (BusinessException e) {
                        assertThat(e.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_OUT_OF_STOCK);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            // then
            int actualQuantity = stockJpaRepository.findByProductId(productId).getQuantity();
            assertThat(actualQuantity).isEqualTo(expectedQuantity);
            assertThat(successCount.get()).isEqualTo(1);
        }
    }
}
