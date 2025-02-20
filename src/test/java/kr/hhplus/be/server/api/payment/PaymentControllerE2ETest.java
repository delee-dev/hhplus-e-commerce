package kr.hhplus.be.server.api.payment;

import io.restassured.RestAssured;
import kr.hhplus.be.server.api.BaseE2ETest;
import kr.hhplus.be.server.api.payment.dto.PayToOrderRequest;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.dto.OrderCommand;
import kr.hhplus.be.server.application.order.dto.OrderItemCommand;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.domain.order.model.OrderStatus;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.Stock;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.point.persistence.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.product.persistence.CategoryJapRepository;
import kr.hhplus.be.server.infrastructure.product.persistence.ProductJpaRepository;
import kr.hhplus.be.server.infrastructure.product.persistence.StockJpaRepository;
import kr.hhplus.be.server.infrastructure.user.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.server.fixture.integration.Fixture.*;
import static org.hamcrest.Matchers.equalTo;

@Sql("/clear.sql")
@EmbeddedKafka(topics = "payment-test")
public class PaymentControllerE2ETest extends BaseE2ETest {
    @Autowired
    private PointFacade pointFacade;
    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private PointJpaRepository pointJpaRepository;
    @Autowired
    private CategoryJapRepository categoryJapRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private StockJpaRepository stockJpaRepository;

    @BeforeEach
    void setUp() {
        User user = user();
        userJpaRepository.saveAndFlush(user);

        Point point = point(user);
        pointJpaRepository.saveAndFlush(point);
        pointFacade.charge(user.getId(), 100_000L);

        Category category = category();
        categoryJapRepository.saveAndFlush(category);

        Product product = product(category, 10_000L);
        productJpaRepository.saveAndFlush(product);

        Stock stock = stock(product, 100);
        stockJpaRepository.saveAndFlush(stock);

        OrderCommand orderCommand = new OrderCommand(user.getId(), List.of(new OrderItemCommand(product.getId(), 5)), "이다은", "010-1234-5678", "서울시 광진구 능동");
        orderFacade.order(orderCommand);
    }

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
                .body("amount", equalTo(50_000))
                .body("orderStatus", equalTo(OrderStatus.PAYMENT_COMPLETED.name()))
                .body("paymentStatus", equalTo(PaymentStatus.COMPLETED.name()));
    }

}
