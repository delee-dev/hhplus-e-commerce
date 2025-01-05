package kr.hhplus.be.server.api.payment.controller;

import kr.hhplus.be.server.api.payment.dto.PayToOrderRequest;
import kr.hhplus.be.server.api.payment.dto.PayToOrderResponse;
import kr.hhplus.be.server.domain.coupon.CouponErrorCode;
import kr.hhplus.be.server.domain.order.model.OrderStatus;
import kr.hhplus.be.server.domain.payment.PaymentErrorCode;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.point.PointErrorCode;
import kr.hhplus.be.server.global.exception.DomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/pay")
public class PaymentController implements PaymentSwaggerApiSpec {
    @Override
    @PatchMapping("/order")
    public ResponseEntity<PayToOrderResponse> payToOrder(@RequestBody PayToOrderRequest request) {
        // 결제 상태
        if (request.orderId() == 9) {
            throw new DomainException(PaymentErrorCode.PAYMENT_ALREADY_COMPLETED);
        } else if (request.orderId() == 99) {
            throw new DomainException(PaymentErrorCode.PAYMENT_ALREADY_CANCELLED);
        }
        // 쿠폰 사용
        request.couponId().ifPresent(couponId -> {
            if (couponId == 9) {
                throw new DomainException(CouponErrorCode.COUPON_ALREADY_USED);
            } else if (couponId == 99) {
                throw new DomainException(CouponErrorCode.COUPON_EXPIRED);
            } else if (couponId == 999) {
                throw new DomainException(CouponErrorCode.COUPON_NOT_APPLICABLE_TO_PAYMENT);
            }
        });

        // 포인트
        if (request.userId() == 9) {
            throw new DomainException(PointErrorCode.POINT_BALANCE_INSUFFICIENT);
        }
        return ResponseEntity.ok(new PayToOrderResponse(request.orderId(), 1, 30000, OrderStatus.PAYMENT_COMPLETED, PaymentStatus.COMPLETED, LocalDateTime.now()));
    }
}
