package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.user.model.User;
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static kr.hhplus.be.server.fixture.integration.Fixture.point;
import static kr.hhplus.be.server.fixture.integration.Fixture.user;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("/clear.sql")
public class PointFacadeIntegrationTest {
    @Autowired
    private PointFacade pointFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;

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
            pointFacade.charge(userId, chargeAmount);

            // then
            Long actualBalance = pointJpaRepository.findByUserId(userId).get().getBalance();
            assertThat(actualBalance).isEqualTo(expectedBalance);
        }
    }

    @Nested
    @DisplayName("포인트 충전 동시성 제어")
    class PointChargeConcurrencyTest {
        @BeforeEach
        void setUp() {
            User user = user();
            userJpaRepository.saveAndFlush(user);

            Point point = point(user);
            pointJpaRepository.saveAndFlush(point);
        }

        @Test
        void 동시에_포인트_충전을_요청하면_하나의_요청만_성공한다() throws InterruptedException {
            // given
            long userId = 1L;
            long chargeAmount = 1_000L;

            int threadCount = 3;
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            AtomicInteger successCount = new AtomicInteger(0);

            // when
            for (int i = 0; i < threadCount; i++) {
                executor.execute(() -> {
                    try {
                        pointFacade.charge(userId, chargeAmount);
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
            assertThat(successCount.get()).isEqualTo(1);
            assertThat(pointJpaRepository.findByUserId(userId))
                    .map(Point::getBalance)
                    .get()
                    .isEqualTo(chargeAmount);
        }
    }
}
