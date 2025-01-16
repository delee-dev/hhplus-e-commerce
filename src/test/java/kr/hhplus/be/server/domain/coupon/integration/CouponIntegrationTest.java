package kr.hhplus.be.server.domain.coupon.integration;

import kr.hhplus.be.server.domain.coupon.CouponErrorCode;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.model.Coupon;
import kr.hhplus.be.server.domain.coupon.model.CouponStatus;
import kr.hhplus.be.server.domain.coupon.model.IssuedCoupon;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.global.exception.BusinessException;
import kr.hhplus.be.server.infrastructure.coupon.persistence.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.coupon.persistence.IssuedCouponJpaRepository;
import kr.hhplus.be.server.infrastructure.user.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static kr.hhplus.be.server.fixture.integration.Fixture.coupon;
import static kr.hhplus.be.server.fixture.integration.Fixture.user;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("/clear.sql")
public class CouponIntegrationTest {
    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponJpaRepository couponJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private IssuedCouponJpaRepository issuedCouponJpaRepository;

    @Nested
    @DisplayName("쿠폰 발행 기능")
    class IssueCouponTest {
        @BeforeEach
        void setUp() {
            User user = user();
            userJpaRepository.saveAndFlush(user);

            Coupon coupon = coupon(100);
            couponJpaRepository.saveAndFlush(coupon);
        }

        @Test
        void 쿠폰이_성공적으로_발행되면_잔여_수량이_차감된다() {
            // given
            Long userId = 1L;
            Long couponId = 1L;

            int quantityBeforeIssue = couponJpaRepository.findById(couponId).get().getTotalQuantity();
            int expectedQuantity = quantityBeforeIssue - 1;

            // when
            IssueCouponCommand command = new IssueCouponCommand(couponId, userId);
            couponService.issueWithLock(command);

            // then
            int actualQuantity = couponJpaRepository.findById(couponId).get().getTotalQuantity();
            assertThat(actualQuantity).isEqualTo(expectedQuantity);
        }

        @Test
        void 쿠폰이_성공적으로_발행되면_발행된_쿠폰이_저장된다() {
            // given
            Long userId = 1L;
            Long couponId = 1L;

            // when
            IssueCouponCommand command = new IssueCouponCommand(couponId, userId);
            couponService.issueWithLock(command);

            // then
            boolean existsIssuedCoupon = issuedCouponJpaRepository.existsByUserIdAndCoupon_Id(userId, couponId);
            assertThat(existsIssuedCoupon).isTrue();
        }
    }

    @Nested
    @DisplayName("쿠폰 사용 기능")
    class UseCouponTest {
        @BeforeEach
        void setUp() {
            User user = user();
            userJpaRepository.saveAndFlush(user);

            Coupon coupon = coupon(100);
            couponJpaRepository.saveAndFlush(coupon);

            IssueCouponCommand command = new IssueCouponCommand(coupon.getId(), user.getId());
            couponService.issueWithLock(command);
        }

        @Test
        void 쿠폰이_성공적으로_사용되면_상태가_사용됨으로_변경된다() {
            // given
            Long userId = 1L;
            Long couponId = 1L;
            Long validOrderAmount = 50_000L;

            // when
            couponService.useWithLock(couponId, userId, validOrderAmount);

            // then
            // FIXME: issuedCouponJpaRepository에 Lock걸지 않는 find 메서드가 없다.. -> 추가?
            IssuedCoupon actualCoupon = issuedCouponJpaRepository.findAll().stream()
                    .filter(coupon -> {
                        return coupon.getCoupon().getId().equals(couponId)
                                && coupon.getUserId().equals(userId);
                    })
                    .findFirst().get();
            assertThat(actualCoupon.getStatus()).isEqualTo(CouponStatus.USED);
        }
    }

    @Nested
    @DisplayName("쿠폰 발행 동시성 제어")
    class IssuedCouponConcurrencyTest {
        @BeforeEach
        void setUp() {
            for (int i = 0; i < 30; i++) {
                User user = user();
                userJpaRepository.saveAndFlush(user);
            }

            Coupon coupon = coupon(100);
            couponJpaRepository.saveAndFlush(coupon);
        }

        @Test
        void 여러_사용자가_동시에_쿠폰_발급을_요청하는_경우_한_번에_하나씩_처리된다() throws InterruptedException {
            // given
            Long couponId = 1L;
            int requestCount = 30;

            int quantityBeforeIssue = couponJpaRepository.findById(couponId).get().getTotalQuantity();
            int expected = quantityBeforeIssue - requestCount;

            // when
            ExecutorService executor = Executors.newFixedThreadPool(requestCount);
            CountDownLatch latch = new CountDownLatch(requestCount);

            AtomicLong userId = new AtomicLong(1L);

            for (int i = 0; i < requestCount; i++) {
                executor.execute(() -> {
                    try {
                        IssueCouponCommand command = new IssueCouponCommand(couponId, userId.getAndIncrement());
                        couponService.issueWithLock(command);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            // then
            int quantityAfterIssue = couponJpaRepository.findById(couponId).get().getTotalQuantity();
            assertThat(quantityAfterIssue).isEqualTo(expected);
        }

    }

    @Nested
    @DisplayName("쿠폰 사용 동시성 제어")
    class UseCouponConcurrencyTest {
        @BeforeEach
        void setUp() {
            User user = user();
            userJpaRepository.saveAndFlush(user);

            Coupon coupon = coupon(100);
            couponJpaRepository.saveAndFlush(coupon);

            IssueCouponCommand command = new IssueCouponCommand(coupon.getId(), user.getId());
            couponService.issueWithLock(command);
        }

        @Test
        void 동일한_쿠폰_사용이_여러번_요청되면_하나의_요청만_성공한다() throws InterruptedException {
            // given
            Long userId = 1L;
            Long couponId = 1L;
            Long validOrderAmount = 50_000L;

            int requestCount = 3;
            ExecutorService executor = Executors.newFixedThreadPool(requestCount);
            CountDownLatch latch = new CountDownLatch(requestCount);

            AtomicInteger successCount = new AtomicInteger(0);

            // when & then
            for (int i = 0; i < requestCount; i++) {
                executor.execute(() -> {
                    try {
                        couponService.useWithLock(userId, couponId, validOrderAmount);
                        successCount.incrementAndGet();
                    } catch (BusinessException e) {
                        assertThat(e.getErrorCode()).isEqualTo(CouponErrorCode.COUPON_ALREADY_USED);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            executor.shutdown();

            // then
            assertThat(successCount.get()).isEqualTo(1);
        }

    }

}
