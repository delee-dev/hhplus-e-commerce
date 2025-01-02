package kr.hhplus.be.server.api.payment.controller;

import kr.hhplus.be.server.api.payment.dto.PayToOrderRequest;
import kr.hhplus.be.server.api.payment.dto.PayToOrderResponse;
import kr.hhplus.be.server.domain.order.model.OrderStatus;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/pay")
public class PaymentController {
    @PatchMapping("/order")
    public ResponseEntity<PayToOrderResponse> payToOrder(@RequestBody PayToOrderRequest request) {
        // 결제 상태
        if (request.orderId() == 9) {
            throw new IllegalStateException("이미 결제 완료된 주문입니다.");
        } else if (request.orderId() == 99) {
            throw new IllegalStateException("이미 결제 취소된 주문입니다.");
        }
        // 쿠폰 사용
        request.couponId().ifPresent(couponId -> {
            if (couponId == 9) {
                throw new IllegalStateException("이미 사용 처리된 쿠폰입니다.");
            } else if (couponId == 99) {
                throw new IllegalStateException("만료된 쿠폰 입니다.");
            } else if (couponId == 999) {
                throw new IllegalStateException("해당 결제에 적용 불가한 쿠폰입니다.");
            }
        });

        // 포인트
        if (request.userId() == 9) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }
        return ResponseEntity.ok(new PayToOrderResponse(request.orderId(), 1, 30000, OrderStatus.PAYMENT_COMPLETED, PaymentStatus.COMPLETED, LocalDateTime.now()));
    }
}
