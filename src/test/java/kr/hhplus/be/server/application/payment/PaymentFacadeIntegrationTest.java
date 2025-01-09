package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.coupon.CouponErrorCode;
import kr.hhplus.be.server.domain.payment.PaymentErrorCode;
import kr.hhplus.be.server.global.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("data.sql")
public class PaymentFacadeIntegrationTest {
    @Autowired
    private PaymentFacade paymentFacade;

    @Test
    void 동시에_같은_주문의_결제를_요청하면_한_건만_결제에_성공한다() throws InterruptedException {
        // given
        long userId = 1L;
        long orderId = 1L;

        PaymentCommand command = new PaymentCommand(userId, orderId, Optional.empty());

        // when & then
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    paymentFacade.pay(command);
                    successCount.getAndIncrement();
                } catch (DomainException e) {
                    assertThat(e)
                            .isInstanceOf(DomainException.class)
                            .hasMessage(PaymentErrorCode.PAYMENT_ALREADY_COMPLETED.getMessage());
                    failureCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(1);
    }

    @Test
    void 동시에_같은_쿠폰_적용을_요청하면_한_건만_결제에_성공한다() throws InterruptedException {
        // given
        long userId = 1L;
        long orderId1 = 1L;
        long orderId2 = 2L;
        long couponId = 1L;

        PaymentCommand command1 = new PaymentCommand(userId, orderId1, Optional.of(couponId));
        PaymentCommand command2 = new PaymentCommand(userId, orderId2, Optional.of(couponId));

        // when
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        executor.execute(() -> {
            try {
                paymentFacade.pay(command1);
                successCount.getAndIncrement();
            } catch (DomainException e) {
                assertThat(e)
                        .isInstanceOf(DomainException.class)
                        .hasMessage(CouponErrorCode.COUPON_ALREADY_USED.getMessage());
                failureCount.getAndIncrement();
            } finally {
                latch.countDown();
            }
        });
        executor.execute(() -> {
            try {
                paymentFacade.pay(command2);
                successCount.getAndIncrement();
            } catch (DomainException e) {
                assertThat(e)
                        .isInstanceOf(DomainException.class)
                        .hasMessage(CouponErrorCode.COUPON_ALREADY_USED.getMessage());
                failureCount.getAndIncrement();
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        executor.shutdown();

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(1);
    }
}
