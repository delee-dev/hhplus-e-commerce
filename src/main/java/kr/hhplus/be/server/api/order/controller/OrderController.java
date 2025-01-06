package kr.hhplus.be.server.api.order.controller;

import kr.hhplus.be.server.api.order.dto.OrderRequest;
import kr.hhplus.be.server.api.order.dto.OrderResponse;
import kr.hhplus.be.server.domain.order.model.OrderStatus;
import kr.hhplus.be.server.domain.product.ProductErrorCode;
import kr.hhplus.be.server.global.exception.DomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/order")
public class OrderController implements OrderSwaggerApiSpec {

    @Override
    @PostMapping
    public ResponseEntity<OrderResponse> order(@RequestBody OrderRequest request) {
        if (request.userId() == 9) {
            throw new DomainException(ProductErrorCode.PRODUCT_OUT_OF_STOCK);
        } else {
            return ResponseEntity
                    .status(201)
                    .body(new OrderResponse(1, 1, OrderStatus.PAYMENT_PENDING, LocalDateTime.now()));
        }
    }
}
