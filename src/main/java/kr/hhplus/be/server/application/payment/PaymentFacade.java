package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.dataplatform.DataPlatformPort;
import kr.hhplus.be.server.application.payment.dto.PaymentCommand;
import kr.hhplus.be.server.application.payment.dto.PaymentResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.UseCouponResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.global.lock.DistributedLockManager;
import kr.hhplus.be.server.global.lock.LockKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentFacade {
    private final PaymentService paymentService;
    private final CouponService couponService;
    private final PointService pointService;
    private final OrderService orderService;
    private final DataPlatformPort dataPlatformPort;

    private final DistributedLockManager lockManager;

    public PaymentResult pay(PaymentCommand command) {
        String key = LockKeyGenerator.generate("pay", List.of(command.orderId()));

        return lockManager.withLock(key, () -> {
            // 결제 유효성 검사
            Payment payment = paymentService.getPaymentByOrderId(command.orderId());
            payment.validatePaymentStatus();

            // 쿠폰 적용
            command.couponId().ifPresent(couponId -> {
                UseCouponResult result = couponService.useWithLock(couponId, command.userId(), payment.getTotalAmount());
                payment.applyDiscount(result.discountAmount());
            });

            // 포인트 사용
            pointService.useWithLock(command.userId(), payment.getFinalAmount());

            // 결제 상태 변경
            payment.complete();

            // 주문 상태 변경
            Order order = orderService.completePayment(payment.getOrderId());

            PaymentResult result = PaymentResult.from(order, payment);
            dataPlatformPort.call(result);
            return result;
        });
    }
}
