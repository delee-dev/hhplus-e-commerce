package kr.hhplus.be.server.domain.coupon.integration;

import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.model.Coupon;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("data.sql")
public class CouponIntegrationTest {
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponRepository couponRepository;

    @Nested
    class IssuedCoupon {
        @Test
        void 여러_사용자가_동시에_쿠폰_발급을_요청하는_경우_한_번에_하나씩_처리된다() throws InterruptedException {
            // given
            Coupon coupon = couponRepository.findById(1L).get();
            int totalQuantity = coupon.getTotalQuantity();

            // when
            int threadCount = 30;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            AtomicLong userId = new AtomicLong(100);

            for (int i = 0; i < threadCount; i++) {
                executor.execute(() -> {
                    try {
                        IssueCouponCommand command = new IssueCouponCommand(coupon.getId(), userId.getAndIncrement());
                        couponService.issueWithLock(command);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            // then
            Coupon couponAfterIssued = couponRepository.findById(1L).get();
            int expected = totalQuantity - threadCount;
            assertThat(couponAfterIssued.getTotalQuantity()).isEqualTo(expected);
        }

    }

}
