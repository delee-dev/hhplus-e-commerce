package kr.hhplus.be.server.api.order;

import io.restassured.RestAssured;
import kr.hhplus.be.server.api.BaseE2ETest;
import kr.hhplus.be.server.api.order.dto.OrderItemRequest;
import kr.hhplus.be.server.api.order.dto.OrderRequest;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.domain.order.model.OrderStatus;
import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.Stock;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.order.persistence.OrderJpaRepository;
import kr.hhplus.be.server.infrastructure.payment.persistence.PaymentJpaRepository;
import kr.hhplus.be.server.infrastructure.point.persistence.PointJpaRepository;
import kr.hhplus.be.server.infrastructure.product.persistence.CategoryJapRepository;
import kr.hhplus.be.server.infrastructure.product.persistence.ProductJpaRepository;
import kr.hhplus.be.server.infrastructure.product.persistence.StockJpaRepository;
import kr.hhplus.be.server.infrastructure.user.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static kr.hhplus.be.server.fixture.integration.Fixture.*;
import static org.hamcrest.Matchers.equalTo;

@Sql("/clear.sql")
public class OrderControllerE2ETest extends BaseE2ETest {
    @Autowired
    private PointFacade pointFacade;

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
    }


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
