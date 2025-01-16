package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderCommand;
import kr.hhplus.be.server.application.order.dto.OrderItemCommand;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.product.ProductErrorCode;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.Stock;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.global.exception.BusinessException;
import kr.hhplus.be.server.infrastructure.order.persistence.OrderJpaRepository;
import kr.hhplus.be.server.infrastructure.payment.persistence.PaymentJpaRepository;
import kr.hhplus.be.server.infrastructure.point.persistence.PointJpaRepository;
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
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static kr.hhplus.be.server.fixture.integration.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("/clear.sql")
public class OrderFacadeIntegrationTest {
    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
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


    @Nested
    @DisplayName("주문 기능")
    class OrderTest {
        @BeforeEach
        void setUp() {
            User user = user();
            userJpaRepository.saveAndFlush(user);

            Point point = point(user);
            pointJpaRepository.saveAndFlush(point);
            pointFacade.charge(user.getId(), 100_000L);

            Category category = category();
            categoryJapRepository.saveAndFlush(category);

            Product product = product(category, 10_000L);
            productJpaRepository.saveAndFlush(product);

            Stock stock = stock(product, 100);
            stockJpaRepository.saveAndFlush(stock);
        }

        @Test
        void 주문_성공_후_재고가_차감된다() {
            // given
            Long userId = 1L;
            Long productId = 1L;
            int quantity = 10;

            int stockBeforeOrder = stockJpaRepository.findByProductId(productId).getQuantity();
            int expectedStock = stockBeforeOrder - quantity;

            OrderCommand command = new OrderCommand(
                    userId,
                    List.of(new OrderItemCommand(productId, quantity)),
                    "이다은",
                    "010-1234-5678",
                    "서울시 광진구 능동"
            );

            // when
            orderFacade.order(command);

            // then
            int actualStock = stockJpaRepository.findByProductId(productId).getQuantity();
            assertThat(actualStock).isEqualTo(expectedStock);
        }

        @Test
        void 주문_성공_후_주문과_결제가_저장된다() {
            // given
            Long userId = 1L;
            Long productId = 1L;
            int quantity = 1;

            OrderCommand command = new OrderCommand(
                    userId,
                    List.of(new OrderItemCommand(productId, quantity)),
                    "이다은",
                    "010-1234-5678",
                    "서울시 광진구 능동"
            );

            // when
            OrderResult orderResult = orderFacade.order(command);

            // then
            assertThat(orderJpaRepository.findById(orderResult.orderId())).isPresent();
            assertThat(paymentJpaRepository.findById(orderResult.paymentId())).isPresent();
        }
    }

    @Nested
    @DisplayName("주문 동시성 제어")
    class OrderConcurrencyTest {
        @BeforeEach
        void setUp() {
            User user1 = user();
            User user2 = user();
            User user3 = user();
            userJpaRepository.saveAllAndFlush(List.of(user1, user2, user3));

            Point point1 = point(user1);
            Point point2 = point(user2);
            Point point3 = point(user3);
            pointJpaRepository.saveAllAndFlush(List.of(point1, point2, point3));
            pointFacade.charge(user1.getId(), 100_000L);
            pointFacade.charge(user2.getId(), 100_000L);
            pointFacade.charge(user3.getId(), 100_000L);

            Category category = category();
            categoryJapRepository.saveAndFlush(category);

            Product product = product(category, 10_000L);
            productJpaRepository.saveAndFlush(product);

            Stock stock = stock(product, 2);
            stockJpaRepository.saveAndFlush(stock);
        }

        @Test
        void 동시에_동일한_상품을_주문하는_경우_한_번에_하나씩_처리된다() throws InterruptedException {
            // given
            AtomicLong userId = new AtomicLong(1);
            long productId = 1L;
            int stockBeforeOrder = stockJpaRepository.findByProductId(productId).getQuantity();

            // when
            int threadCount = 3;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);


            for (int i = 0; i < threadCount; i++) {
                executor.execute(() -> {
                    try {
                        OrderCommand command = new OrderCommand(
                                userId.getAndIncrement(),
                                List.of(new OrderItemCommand(productId, 1)),
                                "이다은",
                                "010-1234-5678",
                                "서울시 광진구 능동"
                        );
                        orderFacade.order(command);
                        successCount.getAndIncrement();
                    } catch (BusinessException e) {
                        assertThat(e)
                                .isInstanceOf(BusinessException.class)
                                .hasMessage(ProductErrorCode.PRODUCT_OUT_OF_STOCK.getMessage());
                        failureCount.getAndIncrement();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            // then
            assertThat(successCount.get()).isEqualTo(stockBeforeOrder);
            assertThat(failureCount.get()).isEqualTo(threadCount - stockBeforeOrder);
        }
    }
}
