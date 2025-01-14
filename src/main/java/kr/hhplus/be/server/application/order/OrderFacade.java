package kr.hhplus.be.server.application.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.order.dto.OrderCommand;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderService orderService;
    private final ProductService productService;
    private final UserService userService;
    private final PaymentService paymentService;

    @Transactional
    public OrderResult order(OrderCommand command) {
        // 유저 조회
        userService.getUser(command.userId());

        // 재고 차감
        List<Product> products = productService.deductStocksWithLock(command.toDeductStockCommands());

        // 주문 생성
        Order order = orderService.createOrder(command.toCreateOrderCommand(products));

        // 결제 생성
        Payment payment = paymentService.createPayment(order.getId(), order.getTotalAmount());

        return OrderResult.from(order, payment);
    }
}
