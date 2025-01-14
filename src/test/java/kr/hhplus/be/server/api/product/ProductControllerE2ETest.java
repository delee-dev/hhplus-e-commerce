package kr.hhplus.be.server.api.product;

import io.restassured.RestAssured;
import kr.hhplus.be.server.api.BaseE2ETest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Sql("data.sql")
public class ProductControllerE2ETest extends BaseE2ETest {
    @Test
    void 상품_조회_E2E_테스트() {
        // given
        long categoryId = 1;
        int page = 0;
        int size = 10;

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
