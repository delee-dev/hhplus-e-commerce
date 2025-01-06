package kr.hhplus.be.server.api.order;

import io.restassured.RestAssured;
import kr.hhplus.be.server.api.BaseE2ETest;
import kr.hhplus.be.server.api.order.dto.OrderItemRequest;
import kr.hhplus.be.server.api.order.dto.OrderRequest;
import kr.hhplus.be.server.domain.order.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

@Sql("data.sql")
public class OrderControllerE2ETest extends BaseE2ETest {
    @Test
    void 주문_E2E_테스트() {
        // given
        long userId = 1;
        List<OrderItemRequest> orderItems = List.of(
                new OrderItemRequest(1L, 2)
        );
        String receiverName = "이다은";
        String receiverPhone = "010-1234-5678";
        String shippingAddress = "서울특별시 강남구 강남대로 123";

        OrderRequest request = new OrderRequest(userId, orderItems, receiverName, receiverPhone, shippingAddress);

        // then
        RestAssured
            .given()
                .header("Content-Type", "application/json")
                .body(request)
            .when()
                .post("/order")
            .then()
                .statusCode(201)
                .body("orderStatus", equalTo(OrderStatus.PAYMENT_PENDING.name()));
    }
}
