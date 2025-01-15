package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.order.model.OrderItem;
import kr.hhplus.be.server.domain.order.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderTest {
    @Nested
    @DisplayName("주문 생성")
    class CreateOrderTest {
        @Test
        void 주문의_총_금액은_개별_아이템의_금액의_합과_같다() {
            // given
            OrderItem item1 = new OrderItem(1L, "무선이어폰", 100000L, 100);
            OrderItem item2 = new OrderItem(2L, "태블릿PC", 200000L, 100);
            List<OrderItem> orderItems = List.of(item1, item2);

            long expectedTotalAmount = (item1.getPrice() * item1.getQuantity()) + (item2.getPrice() * item2.getQuantity());

            // when
            Order order = new Order(1L, orderItems, "이다은", "010-1234-1234", "서울시 광진구 능동");

            // then
            long actualTotalAmount = order.getTotalAmount();
            assertThat(actualTotalAmount).isEqualTo(expectedTotalAmount);
        }

        @Test
        void 주문을_생성할_때_OrderItem에_Order가_설정된다() {
            // given
            OrderItem item1 = new OrderItem(1L, "무선이어폰", 100000L, 100);
            OrderItem item2 = new OrderItem(2L, "태블릿PC", 200000L, 100);
            List<OrderItem> orderItems = List.of(item1, item2);

            // when
            Order order = new Order(1L, orderItems, "이다은", "010-1234-1234", "서울시 광진구 능동");

            // then
            assertThat(item1.getOrder()).isEqualTo(order);
            assertThat(item2.getOrder()).isEqualTo(order);
        }

        @Test
        void 주문_생성_후_상태는_PAYMENT_PENDING_이다() {
            // given
            OrderItem item1 = new OrderItem(1L, "무선이어폰", 100000L, 100);
            OrderItem item2 = new OrderItem(2L, "태블릿PC", 200000L, 100);
            List<OrderItem> orderItems = List.of(item1, item2);

            // when
            Order order = new Order(1L, orderItems, "이다은", "010-1234-1234", "서울시 광진구 능동");

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING);
        }
    }
}
