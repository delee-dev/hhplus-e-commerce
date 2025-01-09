package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderCommand;
import kr.hhplus.be.server.application.order.dto.OrderItemCommand;
import kr.hhplus.be.server.domain.product.ProductErrorCode;
import kr.hhplus.be.server.domain.product.StockRepository;
import kr.hhplus.be.server.global.exception.DomainException;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("data.sql")
public class OrderFacadeIntegrationTest {
    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private StockRepository stockRepository;

    @Test
    void 동시에_동일한_상품을_주문하는_경우_한_번에_하나씩_처리된다() throws InterruptedException {
        // given
        AtomicLong userId = new AtomicLong(1);
        long productId = 1L;
        int currentStockQuantity = stockRepository.findByProductId(productId).getQuantity();

        // when & then
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
                            "이충헌",
                            "010-1234-5678",
                            "강남구 강남대로 1234"
                    );
                    orderFacade.order(command);
                    successCount.getAndIncrement();
                } catch (DomainException e) {
                    assertThat(e)
                            .isInstanceOf(DomainException.class)
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
        assertThat(successCount.get()).isEqualTo(currentStockQuantity);
        assertThat(failureCount.get()).isEqualTo(threadCount - currentStockQuantity);
    }
}
