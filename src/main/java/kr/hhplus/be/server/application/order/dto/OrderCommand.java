package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.dto.CreateOrderCommand;
import kr.hhplus.be.server.domain.order.dto.CreateOrderItemCommand;
import kr.hhplus.be.server.domain.product.dto.DeductStockCommand;
import kr.hhplus.be.server.domain.product.model.Product;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record OrderCommand(
        long userId,
        List<OrderItemCommand> orderItems,
        String receiverName,
        String receiverPhone,
        String shippingAddress
) {
    public List<DeductStockCommand> toDeductStockCommands() {
        return orderItems.stream().map(OrderItemCommand::toDeductStockCommand).toList();
    }

    public CreateOrderCommand toCreateOrderCommand(List<Product> products) {
        Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
        List<CreateOrderItemCommand> createItemCommands = orderItems.stream()
                .map(orderItem -> {
                    Product product = productMap.get(orderItem.productId());
                    return orderItem.toCreateOrderCommand(product);
                }).toList();

        return new CreateOrderCommand(userId, createItemCommands, receiverName, receiverPhone, shippingAddress);
    }
}
