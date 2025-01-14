package kr.hhplus.be.server.api.order.controller;

import kr.hhplus.be.server.api.order.dto.OrderRequest;
import kr.hhplus.be.server.api.order.dto.OrderResponse;
import kr.hhplus.be.server.application.order.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController implements OrderSwaggerApiSpec {
    private final OrderFacade orderFacade;

    @Override
    @PostMapping
    public ResponseEntity<OrderResponse> order(@RequestBody OrderRequest request) {
        OrderResponse response = OrderResponse.from(orderFacade.order(request.to()));

        return ResponseEntity
                .status(201)
                .body(response);
    }
}
