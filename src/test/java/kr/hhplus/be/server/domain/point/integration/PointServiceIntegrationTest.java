package kr.hhplus.be.server.domain.point.integration;

import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.point.model.PointHistory;
import kr.hhplus.be.server.domain.point.model.TransactionType;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.point.persistence.PointHistoryJpaRepository;
import kr.hhplus.be.server.infrastructure.point.persistence.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.user.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static kr.hhplus.be.server.fixture.integration.Fixture.point;
import static kr.hhplus.be.server.fixture.integration.Fixture.user;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("/clear.sql")
public class PointServiceIntegrationTest {
    @Autowired
    private PointService pointService;

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private PointHistoryJpaRepository pointHistoryJpaRepository;

    @Nested
    @DisplayName("포인트 충전 기능")
    class PointChargeTest {
        @BeforeEach
        void setUp() {
            User user = user();
            userJpaRepository.saveAndFlush(user);

            Point point = point(user);
            pointJpaRepository.saveAndFlush(point);
        }

        @Test
        void 포인트_충전_후_충전_금액만큼_잔액이_증가한다() {
            // given
            Long userId = 1L;
            Long chargeAmount = 1_000L;

            Long balanceBeforeCharge = pointJpaRepository.findByUserId(userId).get().getBalance();
            Long expectedBalance = balanceBeforeCharge + chargeAmount;

            // when
            pointService.chargeWithLock(userId, chargeAmount);

            // then
            Long actualBalance = pointJpaRepository.findByUserId(userId).get().getBalance();
            assertThat(actualBalance).isEqualTo(expectedBalance);
        }

        @Test
        void 포인트_충전_후_충전_내역이_추가된다() {
            // given
            Long userId = 1L;
            Long chargeAmount = 1_000L;

            // when
            pointService.chargeWithLock(userId, chargeAmount);

            // then
            List<PointHistory> histories =  pointHistoryJpaRepository.findAll();
            assertThat(histories).hasSize(1);
            assertThat(histories.get(0).getType()).isEqualTo(TransactionType.CHARGE);
        }
    }

    @Nested
    @DisplayName("포인트 사용 기능")
    class PointUseTest {
        @BeforeEach
        void setUp() {
            User user = user();
            userJpaRepository.saveAndFlush(user);

            Point point = point(user);
            pointJpaRepository.saveAndFlush(point);

            pointService.chargeWithLock(user.getId(), 100_000L);
        }

        @Test
        void 포인트_사용_후_사용_금액만큼_잔액이_감소한다() {
            // given
            Long userId = 1L;
            Long useAmount = 10_000L;

            Long balanceBeforeUse = pointJpaRepository.findByUserId(userId).get().getBalance();
            Long expectedBalance = balanceBeforeUse - useAmount;

            // when
            pointService.useWithLock(userId, useAmount);

            // then
            Long actualBalance = pointJpaRepository.findByUserId(userId).get().getBalance();
            assertThat(actualBalance).isEqualTo(expectedBalance);
        }

        @Test
        void 포인트_충전_후_충전_내역이_추가된다() {
            // given
            Long userId = 1L;
            Long useAmount = 10_000L;

            // when
            pointService.useWithLock(userId, useAmount);

            // then
            List<PointHistory> histories =  pointHistoryJpaRepository.findAll();
            assertThat(histories).hasSize(2);
            assertThat(histories.get(1).getType()).isEqualTo(TransactionType.USE);
        }

    }

    @Nested
    @DisplayName("포인트 동시성 제어")
    class PointConcurrencyTest {
        @BeforeEach
        void setUp() {
            User user = user();
            userJpaRepository.saveAndFlush(user);

            Point point = point(user);
            pointJpaRepository.saveAndFlush(point);

            pointService.chargeWithLock(user.getId(), 100_000L);
        }

        @Test
        void 동시에_포인트_충전을_요청하면_하나의_요청만_성공한다() throws InterruptedException {
            // given
            Long userId = 1L;
            Long chargeAmount = 1_000L;
            Long balanceBeforeCharge = pointJpaRepository.findByUserId(userId).get().getBalance();
            Long expectedBalance = balanceBeforeCharge + chargeAmount;

            int threadCount = 3;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            AtomicInteger successCount = new AtomicInteger(0);

            // when
            for (int i = 0; i < threadCount; i++) {
                executor.execute(() -> {
                    try {
                        pointService.chargeWithLock(userId, chargeAmount);
                        successCount.incrementAndGet();
                    } catch (OptimisticLockingFailureException ignored) {
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            executor.shutdown();

            // then
            Long actualBalance = pointJpaRepository.findByUserId(userId).get().getBalance();

            assertThat(successCount.get()).isEqualTo(1);
            assertThat(actualBalance).isEqualTo(expectedBalance);
        }

        @Test
        void 동시에_포인트_사용을_요청하면_하나의_요청만_성공한다() throws InterruptedException {
            // given
            Long userId = 1L;
            Long chargeAmount = 1_000L;
            Long balanceBeforeUse = pointJpaRepository.findByUserId(userId).get().getBalance();
            Long expectedBalance = balanceBeforeUse - chargeAmount;

            int threadCount = 3;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            AtomicInteger successCount = new AtomicInteger(0);

            // when
            for (int i = 0; i < threadCount; i++) {
                executor.execute(() -> {
                    try {
                        pointService.useWithLock(userId, chargeAmount);
                        successCount.incrementAndGet();
                    } catch (OptimisticLockingFailureException ignored) {
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            executor.shutdown();

            // then
            Long actualBalance = pointJpaRepository.findByUserId(userId).get().getBalance();

            assertThat(successCount.get()).isEqualTo(1);
            assertThat(actualBalance).isEqualTo(expectedBalance);
        }

        @Test
        void 동시에_동일한_포인트에_접근하면_낙관적_락_예외가_발생한다() throws InterruptedException {
            // given
            Long userId = 1L;
            Long amount = 1_000L;

            int threadCount = 2;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            AtomicInteger optimisticLockCount = new AtomicInteger(0);

            // when
            executor.execute(() -> {
                try {
                    pointService.chargeWithLock(userId, amount);
                } catch (OptimisticLockingFailureException e) {
                    optimisticLockCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });

            executor.execute(() -> {
                try {
                    pointService.useWithLock(userId, amount);
                } catch (OptimisticLockingFailureException e) {
                    optimisticLockCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });

            latch.await();
            executor.shutdown();

            // then
            assertThat(optimisticLockCount.get()).isGreaterThan(0);
        }

    }
}
