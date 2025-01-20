package kr.hhplus.be.server.infrastructure.product.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.product.model.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.domain.order.model.QOrder.order;
import static kr.hhplus.be.server.domain.order.model.QOrderItem.orderItem;
import static kr.hhplus.be.server.domain.payment.model.QPayment.payment;
import static kr.hhplus.be.server.domain.product.model.QCategory.category;
import static kr.hhplus.be.server.domain.product.model.QProduct.product;

@Component
public class ProductQueryRepository {
    private final JPAQueryFactory queryFactory;

    public ProductQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<Product> findBestSellingProductsByCategory(Long categoryId, int period, int limit) {
        LocalDateTime end = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime start = end.minusDays(period);

        return queryFactory
                .select(product)
                .from(orderItem)
                .join(product).on(orderItem.productId.eq(product.id))
                .join(product.category, category)
                .join(orderItem.order, order)
                .join(payment).on(orderItem.order.id.eq(payment.orderId))
                .where(
                        category.id.eq(categoryId)
                                .and(payment.paidAt.between(
                                        start,
                                        end
                                ))
                                .and(payment.status.eq(PaymentStatus.COMPLETED))
                )
                .groupBy(product)
                .orderBy(orderItem.quantity.sum().desc())
                .limit(limit)
                .fetch();
    }

}
