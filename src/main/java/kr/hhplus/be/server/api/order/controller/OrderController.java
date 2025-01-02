package kr.hhplus.be.server.api.order.controller;

import kr.hhplus.be.server.api.order.dto.OrderRequest;
import kr.hhplus.be.server.api.order.dto.OrderResponse;
import kr.hhplus.be.server.domain.order.model.OrderStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/order")
public class OrderController {
    @PostMapping
    public ResponseEntity<OrderResponse> order(@RequestBody OrderRequest request) {
        if (request.userId() == 9) {
            throw new IllegalStateException("주문한 상품의 재고가 부족합니다.");
        } else {
            return ResponseEntity.ok(new OrderResponse(1, 1, OrderStatus.PAYMENT_PENDING, LocalDateTime.now()));
        }
    }
}
