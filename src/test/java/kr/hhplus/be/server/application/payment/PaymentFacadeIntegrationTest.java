package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.dataplatform.DataPlatformPort;
import kr.hhplus.be.server.application.dataplatform.dto.PaymentInfo;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.dto.OrderCommand;
import kr.hhplus.be.server.application.order.dto.OrderItemCommand;
import kr.hhplus.be.server.application.payment.dto.PaymentCommand;
import kr.hhplus.be.server.application.payment.dto.PaymentResult;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.domain.coupon.CouponErrorCode;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.model.Coupon;
import kr.hhplus.be.server.domain.coupon.model.CouponStatus;
import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.order.model.OrderStatus;
import kr.hhplus.be.server.domain.payment.PaymentErrorCode;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.point.PointErrorCode;
import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.Stock;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.global.exception.BusinessException;
import kr.hhplus.be.server.infrastructure.coupon.persistence.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.coupon.persistence.IssuedCouponJpaRepository;
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
import org.redisson.api.RAtomicLong;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static kr.hhplus.be.server.fixture.integration.Fixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Sql("/clear.sql")
public class PaymentFacadeIntegrationTest {
    @Autowired
    private PaymentFacade paymentFacade;

    @MockitoSpyBean
    private DataPlatformPort dataPlatformPort;

    @Autowired
    private PointFacade pointFacade;
    @Autowired
    private OrderFacade orderFacade;
    @Autowired
    private CouponService couponService;

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
    @Autowired
    private CouponJpaRepository couponJpaRepository;
    @Autowired
    private IssuedCouponJpaRepository issuedCouponJpaRepository;
    @Autowired
    private RedissonClient redissonClient;

    @Nested
    @DisplayName("기본 결제 기능")
    class PaymentTest {
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

            OrderCommand orderCommand = new OrderCommand(user.getId(), List.of(new OrderItemCommand(product.getId(), 5)), "이다은", "010-1234-5678", "서울시 광진구 능동");
            orderFacade.order(orderCommand);
        }

        @Test
        void 결제_성공_후_주문과_결제_상태가_변경된다() {
            // given
            Long userId = 1L;
            Long orderId = 1L;

            PaymentCommand command = new PaymentCommand(userId, orderId, Optional.empty());

            // when
            PaymentResult result = paymentFacade.pay(command);

            // then
            assertThat(orderJpaRepository.findById(result.orderId()))
                    .map(Order::getStatus)
                    .get()
                    .isEqualTo(OrderStatus.PAYMENT_COMPLETED);
            assertThat(paymentJpaRepository.findById(result.paymentId()))
                    .map(Payment::getStatus)
                    .get()
                    .isEqualTo(PaymentStatus.COMPLETED);
        }

        @Test
        void 결제_성공_후_결제_금액만큼_포인트가_차감된다() {
            // given
            Long userId = 1L;
            Long orderId = 1L;

            Point point = pointJpaRepository.findByUserId(userId).get();
            Long balanceBeforePay = point.getBalance();

            Payment payment = paymentJpaRepository.findById(orderId).get();
            Long amountForPay = payment.getFinalAmount();

            Long expectedPoint = balanceBeforePay - amountForPay;

            PaymentCommand command = new PaymentCommand(userId, orderId, Optional.empty());

            // when
            paymentFacade.pay(command);

            // then
            assertThat(pointJpaRepository.findByUserId(userId))
                    .map(Point::getBalance)
                    .get()
                    .isEqualTo(expectedPoint);
        }

        @Test
        void 결제_성공_후_데이터_플랫폼으로_결제_정보가_전송된다() {
            // given
            Long userId = 1L;
            Long orderId = 1L;

            PaymentCommand command = new PaymentCommand(userId, orderId, Optional.empty());

            // when
            paymentFacade.pay(command);

            // then
            verify(dataPlatformPort, times(1)).sendPaymentInfo(any(PaymentInfo.class));
        }
    }

    @Nested
    @DisplayName("쿠폰 적용 결제")
    class PaymentWithCoupon {
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

            OrderCommand orderCommand = new OrderCommand(user.getId(), List.of(new OrderItemCommand(product.getId(), 5)), "이다은", "010-1234-5678", "서울시 광진구 능동");
            orderFacade.order(orderCommand);

            Coupon coupon = coupon(100);
            couponJpaRepository.saveAndFlush(coupon);

            RAtomicLong quantity = redissonClient.getAtomicLong(String.join(":", "coupon:quantity", coupon.getId().toString()));
            quantity.delete();

            RSet<Long> issuedUserSet = redissonClient.getSet(String.join(":", "coupon:issued", coupon.getId().toString()));
            issuedUserSet.delete();

            couponService.initializeCouponQuantity(coupon.getId());

            IssueCouponCommand issueCouponCommand = new IssueCouponCommand(user.getId(), coupon.getId());
            couponService.issue(issueCouponCommand);
        }

        @Test
        void 결제_성공_후_쿠폰_상태가_사용됨으로_변경된다() {
            // given
            Long userId = 1L;
            Long orderId = 1L;
            Long couponId = 1L;
            Long issuedCouponId = 1L;

            PaymentCommand command = new PaymentCommand(userId, orderId, Optional.of(couponId));

            // when
            paymentFacade.pay(command);

            // then
            CouponStatus actualStatus = issuedCouponJpaRepository.findById(issuedCouponId).get().getStatus();
            assertThat(actualStatus).isEqualTo(CouponStatus.USED);
        }
    }

    @Nested
    @DisplayName("트랜잭션 롤백")
    class PaymentRollbackTest {
        @BeforeEach
        void setUp() {
            User user = user();
            userJpaRepository.saveAndFlush(user);

            Point point = point(user);
            pointJpaRepository.saveAndFlush(point);
            pointFacade.charge(user.getId(), 100_000L);

            Category category = category();
            categoryJapRepository.saveAndFlush(category);

            Product product = product(category, 110_000L);
            productJpaRepository.saveAndFlush(product);

            Stock stock = stock(product, 100);
            stockJpaRepository.saveAndFlush(stock);

            OrderCommand orderCommand = new OrderCommand(user.getId(), List.of(new OrderItemCommand(product.getId(), 5)), "이다은", "010-1234-5678", "서울시 광진구 능동");
            orderFacade.order(orderCommand);

            Coupon coupon = coupon(100);
            couponJpaRepository.saveAndFlush(coupon);

            RAtomicLong quantity = redissonClient.getAtomicLong(String.join(":", "coupon:quantity", coupon.getId().toString()));
            quantity.delete();

            RSet<Long> issuedUserSet = redissonClient.getSet(String.join(":", "coupon:issued", coupon.getId().toString()));
            issuedUserSet.delete();

            couponService.initializeCouponQuantity(coupon.getId());

            IssueCouponCommand issueCouponCommand = new IssueCouponCommand(user.getId(), coupon.getId());
            couponService.issue(issueCouponCommand);
        }

        @Test
        void 포인트_사용_중_실패하면_쿠폰_사용도_롤백된다() {
            // given
            Long userId = 1L;
            Long orderId = 1L;
            Long couponId = 1L;
            Long issuedCouponId = 1L;

            PaymentCommand command = new PaymentCommand(userId, orderId, Optional.of(couponId));

            // when
            try {
                paymentFacade.pay(command);
            } catch (BusinessException e) {
                if (!e.getErrorCode().equals(PointErrorCode.POINT_BALANCE_INSUFFICIENT)) {
                    throw e;
                }
            }

            // then
            CouponStatus actualStatus = issuedCouponJpaRepository.findById(issuedCouponId).get().getStatus();
            assertThat(actualStatus).isEqualTo(CouponStatus.AVAILABLE);
        }

    }

    @Nested
    @DisplayName("결제 동시성 제어")
    class PaymentConcurrencyTest {
        @BeforeEach
        void setUp() {
            User user1 = user();
            User user2 = user();
            userJpaRepository.saveAllAndFlush(List.of(user1, user2));

            Point point1 = point(user1);
            Point point2 = point(user2);
            pointJpaRepository.saveAllAndFlush(List.of(point1, point2));
            pointFacade.charge(user1.getId(), 100_000L);
            pointFacade.charge(user2.getId(), 100_000L);

            Category category = category();
            categoryJapRepository.saveAndFlush(category);

            Product product = product(category, 10_000L);
            productJpaRepository.saveAndFlush(product);

            Stock stock = stock(product, 100);
            stockJpaRepository.saveAndFlush(stock);

            OrderCommand orderCommand1 = new OrderCommand(user1.getId(), List.of(new OrderItemCommand(product.getId(), 1)), "이다은", "010-1234-5678", "서울시 광진구 능동");
            orderFacade.order(orderCommand1);
            OrderCommand orderCommand2 = new OrderCommand(user2.getId(), List.of(new OrderItemCommand(product.getId(), 1)), "이다은", "010-1234-5678", "서울시 광진구 능동");
            orderFacade.order(orderCommand2);

            Coupon coupon = coupon(100);
            couponJpaRepository.saveAndFlush(coupon);

            RAtomicLong quantity = redissonClient.getAtomicLong(String.join(":", "coupon:quantity", coupon.getId().toString()));
            quantity.delete();

            RSet<Long> issuedUserSet = redissonClient.getSet(String.join(":", "coupon:issued", coupon.getId().toString()));
            issuedUserSet.delete();

            couponService.initializeCouponQuantity(coupon.getId());

            IssueCouponCommand issueCouponCommand = new IssueCouponCommand(user1.getId(), coupon.getId());
            couponService.issue(issueCouponCommand);
        }

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
                    } catch (BusinessException e) {
                        assertThat(e)
                                .isInstanceOf(BusinessException.class)
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
            long couponId = 1L;
            long orderId1 = 1L;
            long orderId2 = 2L;

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
                } catch (OptimisticLockingFailureException e) {
                    failureCount.getAndIncrement();
                } catch (BusinessException e) {
                    assertThat(e.getMessage()).isEqualTo(CouponErrorCode.COUPON_ALREADY_USED.getMessage());
                    failureCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
            executor.execute(() -> {
                try {
                    paymentFacade.pay(command2);
                    successCount.getAndIncrement();
                } catch (OptimisticLockingFailureException e) {
                    failureCount.getAndIncrement();
                } catch (BusinessException e) {
                    assertThat(e.getMessage()).isEqualTo(CouponErrorCode.COUPON_ALREADY_USED.getMessage());
                    failureCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });

            latch.await();
            executor.shutdown();

            // then
            assertThat(successCount.get()).isEqualTo(1);
            assertThat(failureCount.get()).isEqualTo(threadCount - 1);
        }
    }
}
