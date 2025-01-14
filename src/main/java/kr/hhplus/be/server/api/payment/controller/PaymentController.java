package kr.hhplus.be.server.api.payment.controller;

import kr.hhplus.be.server.api.payment.dto.PayToOrderRequest;
import kr.hhplus.be.server.api.payment.dto.PayToOrderResponse;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PaymentController implements PaymentSwaggerApiSpec {
    private final PaymentFacade paymentFacade;

    @Override
    @PatchMapping("/order")
    public ResponseEntity<PayToOrderResponse> payToOrder(@RequestBody PayToOrderRequest request) {
        PayToOrderResponse response = PayToOrderResponse.from(paymentFacade.pay(request.to()));

        return ResponseEntity.ok(response);
    }
}
