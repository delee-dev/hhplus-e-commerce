package kr.hhplus.be.server.application.payment;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.payment.dto.PaymentCommand;
import kr.hhplus.be.server.application.payment.dto.PaymentResult;
import kr.hhplus.be.server.application.dataplatform.DataPlatformPort;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.UseCouponResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {
    private final PaymentService paymentService;
    private final CouponService couponService;
    private final PointService pointService;
    private final OrderService orderService;
    private final DataPlatformPort dataPlatformPort;

    @Transactional
    public PaymentResult pay(PaymentCommand command) {
        // 결제 유효성 검사
        Payment payment = paymentService.getPaymentByOrderIdWithLock(command.orderId());
        payment.validatePaymentEligibility();

        // 쿠폰 적용
        command.couponId().ifPresent(couponId -> {
            UseCouponResult result = couponService.useWithLock(couponId, command.userId(), payment.getTotalAmount());
            payment.applyDiscount(result.discountAmount());
        });

        // 포인트 사용
        pointService.useWithLock(command.userId(), payment.getFinalAmount());

        // 결제 상태 변경
        payment.completePayment();
        paymentService.save(payment);

        // 주문 상태 변경
        Order order = orderService.getOrder(payment.getOrderId());
        order.completePayment();
        orderService.save(order);

        PaymentResult result = PaymentResult.fromEntity(order, payment);

        dataPlatformPort.call(result);

        return result;
    }
}
