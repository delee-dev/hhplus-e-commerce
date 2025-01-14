package kr.hhplus.be.server.application.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.application.order.dto.OrderCommand;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.CreateOrderCommand;
import kr.hhplus.be.server.domain.order.dto.CreateOrderItemCommand;
import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.dto.DeductStockCommand;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        User user = userService.getUser(command.userId());

        // 재고 차감
        List<DeductStockCommand> deductStockCommands = command.orderItems().stream()
                .map(orderItemCommand -> new DeductStockCommand(orderItemCommand.productId(), orderItemCommand.quantity()))
                .toList();
        List<Product> products = productService.deductStocksWithLock(deductStockCommands);

        // 주문 생성
        Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        List<CreateOrderItemCommand> createOrderItemCommands = command.orderItems().stream()
                .map(orderItem -> {
                    Product product = productMap.get(orderItem.productId());
                     return new CreateOrderItemCommand(
                            orderItem.productId(),
                            product.getName(),
                            product.getPrice(),
                            orderItem.quantity()
                     );
                }).toList();
        CreateOrderCommand createOrderCommand = new CreateOrderCommand(
                user.getId(),
                createOrderItemCommands,
                command.receiverName(),
                command.receiverPhone(),
                command.shippingAddress()
        );
        Order order = orderService.createOrder(createOrderCommand);

        // 결제 생성
        Payment payment = paymentService.createPayment(order.getId(), order.getTotalAmount());

        return OrderResult.from(order, payment);
    }
}
