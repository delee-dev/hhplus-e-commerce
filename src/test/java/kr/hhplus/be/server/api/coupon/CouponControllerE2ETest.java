package kr.hhplus.be.server.api.coupon;

import io.restassured.RestAssured;
import kr.hhplus.be.server.api.BaseE2ETest;
import kr.hhplus.be.server.api.coupon.dto.IssueCouponRequest;
import kr.hhplus.be.server.domain.coupon.model.Coupon;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.coupon.persistence.CouponJpaRepository;
import kr.hhplus.be.server.infrastructure.user.persistence.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static kr.hhplus.be.server.fixture.integration.Fixture.coupon;
import static kr.hhplus.be.server.fixture.integration.Fixture.user;
import static org.hamcrest.Matchers.equalTo;

@Sql("/clear.sql")
public class CouponControllerE2ETest extends BaseE2ETest {
    @Autowired
    private CouponJpaRepository couponJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        User user = user();
        userJpaRepository.saveAndFlush(user);

        Coupon coupon = coupon(100);
        couponJpaRepository.saveAndFlush(coupon);
    }

    @Test
    void 쿠폰_발행_E2E_테스트() {
        // when
        long couponId = 1;
        long userId = 1;
        IssueCouponRequest request = new IssueCouponRequest(couponId, userId);

        // then
        RestAssured
            .given()
                .header("Content-Type", "application/json")
                .body(request)
            .when()
                .post("/coupon/issue")
            .then()
                .statusCode(201)
                .body("success", equalTo(true));
    }
}
