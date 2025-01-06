package kr.hhplus.be.server.api.payment;

import io.restassured.RestAssured;
import kr.hhplus.be.server.api.BaseE2ETest;
import kr.hhplus.be.server.api.payment.dto.PayToOrderRequest;
import kr.hhplus.be.server.domain.order.model.OrderStatus;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;

@Sql("data.sql")
public class PaymentControllerE2ETest extends BaseE2ETest {
    @Test
    void 결제_E2E_테스트() {
        // given
        long userId = 1;
        long orderId = 1;
        Optional<Long> couponId = Optional.empty();
        PayToOrderRequest request = new PayToOrderRequest(userId, orderId, couponId);

        // then
        RestAssured
            .given()
                .header("Content-Type", "application/json")
                .body(request)
            .when()
                .patch("/pay/order")
            .then()
                .statusCode(200)
                .body("orderId", equalTo(1))
                .body("amount", equalTo(100000))
                .body("orderStatus", equalTo(OrderStatus.PAYMENT_COMPLETED.name()))
                .body("paymentStatus", equalTo(PaymentStatus.COMPLETED.name()));
    }

}
