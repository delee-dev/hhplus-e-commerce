package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.application.point.dto.PointResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("data.sql")
public class PointFacadeIntegrationTest {
    @Autowired
    private PointFacade pointFacade;

    @Test
    void 동시에_포인트_충전_요청시_낙관적_락_예외가_발생한다() throws InterruptedException {
        // given
        long userId = 1L;
        long chargeAmount = 1_000L;

        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    pointFacade.charge(userId, chargeAmount);
                    successCount.incrementAndGet();
                } catch (OptimisticLockingFailureException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        // then
        assertThat(failCount.get()).isGreaterThan(0);
    }

    @Test
    void 동시_포인트_요청으로_낙관적_락이_발생할_때_포인트_충전이_취소된다() throws InterruptedException {
        // given
        long userId = 1L;
        long chargeAmount = 1_000L;
        long initialBalance = pointFacade.getPoint(userId).balance();

        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    pointFacade.charge(userId, chargeAmount);
                    successCount.incrementAndGet();
                } catch (OptimisticLockingFailureException e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        // then
        long expectedBalance = initialBalance + (successCount.get() * chargeAmount);
        PointResult actual = pointFacade.getPoint(userId);

        assertThat(actual.balance()).isEqualTo(expectedBalance);
    }
}
