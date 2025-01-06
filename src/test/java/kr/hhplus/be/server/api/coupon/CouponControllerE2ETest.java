package kr.hhplus.be.server.api.coupon;

import io.restassured.RestAssured;
import kr.hhplus.be.server.api.BaseE2ETest;
import kr.hhplus.be.server.api.coupon.dto.IssueCouponRequest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.equalTo;

@Sql("data.sql")
public class CouponControllerE2ETest extends BaseE2ETest {
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
