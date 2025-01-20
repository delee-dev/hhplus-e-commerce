package kr.hhplus.be.server.api.product;

import io.restassured.RestAssured;
import kr.hhplus.be.server.api.BaseE2ETest;
import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.order.persistence.OrderJpaRepository;
import kr.hhplus.be.server.infrastructure.payment.persistence.PaymentJpaRepository;
import kr.hhplus.be.server.infrastructure.product.persistence.CategoryJapRepository;
import kr.hhplus.be.server.infrastructure.product.persistence.ProductJpaRepository;
import kr.hhplus.be.server.infrastructure.user.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static kr.hhplus.be.server.fixture.integration.Fixture.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Sql("/clear.sql")
public class ProductControllerE2ETest extends BaseE2ETest {
    @Autowired
    private CategoryJapRepository categoryJapRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private OrderJpaRepository orderJpaRepository;
    @Autowired
    private PaymentJpaRepository paymentJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        User user = user();
        userJpaRepository.saveAndFlush(user);

        Category category = category();
        categoryJapRepository.saveAndFlush(category);

        List<Product> products = products(category);
        productJpaRepository.saveAllAndFlush(products);

        List<Order> orders = orders(user, products);
        orderJpaRepository.saveAllAndFlush(orders);

        List<Payment> payments = payments(orders);
        paymentJpaRepository.saveAllAndFlush(payments);
    }

    @Test
    void 상품_조회_E2E_테스트() {
        // given
        long categoryId = 1;
        int page = 0;
        int size = 5;

        // then
        RestAssured
            .given()
                .queryParam("categoryId", categoryId)
                .queryParam("page", page)
                .queryParam("size", size)
            .when()
                .get("/products")
            .then()
                .statusCode(200)
                .body("content", hasSize(size));
    }

    @Test
    void 베스트_셀러_조회_E2E_테스트() {
        // given
        long categoryId = 1;

        // then
        RestAssured
                .given()
                .queryParam("categoryId", categoryId)
                .when()
                .get("/products/best")
                .then()
                .statusCode(200)
                .body("size()", equalTo(3)); // 3일간 3개 상품만 거래 발생
    }
}
