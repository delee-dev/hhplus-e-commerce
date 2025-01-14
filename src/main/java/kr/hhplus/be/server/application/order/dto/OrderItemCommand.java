package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.dto.CreateOrderItemCommand;
import kr.hhplus.be.server.domain.product.dto.DeductStockCommand;
import kr.hhplus.be.server.domain.product.model.Product;

public record OrderItemCommand(
        long productId,
        int quantity
) {
    public DeductStockCommand toDeductStockCommand() {
        return new DeductStockCommand(productId, quantity);
    }

    public CreateOrderItemCommand toCreateOrderCommand(Product product) {
        return new CreateOrderItemCommand(productId, product.getName(), product.getPrice(), quantity);
    }
}
