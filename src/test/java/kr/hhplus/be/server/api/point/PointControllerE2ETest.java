package kr.hhplus.be.server.api.point;

import io.restassured.RestAssured;
import kr.hhplus.be.server.api.BaseE2ETest;
import kr.hhplus.be.server.api.point.dto.ChargePointRequest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.equalTo;

@Sql("data.sql")
public class PointControllerE2ETest extends BaseE2ETest {
    @Test
    void 포인트_조회_E2E_테스트() {
        // given
        long userId = 1;

        // then
        RestAssured
            .given()
                .queryParam("userId", userId)
            .when()
                .get("/point")
            .then()
                .statusCode(200)
                .body("userId", equalTo(1))
                .body("userName", equalTo("이다은"))
                .body("balance", equalTo(1000000));
    }

    @Test
    void 포인트_충전_E2E_테스트() {
        // given
        long userId = 1;
        long amount = 10_000;
        ChargePointRequest request = new ChargePointRequest(userId, amount);

        // then
        RestAssured
            .given()
                .header("Content-Type", "application/json")
                .body(request)
            .when()
                .patch("/point/charge")
            .then()
                .statusCode(200)
                .body("userId", equalTo(1))
                .body("userName", equalTo("이다은"))
                .body("balance", equalTo(1010000));
    }
}
