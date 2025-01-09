package kr.hhplus.be.server.domain.product.integration;

import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.dto.GetProductsQuery;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.global.model.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql("data.sql")
public class ProductServiceIntegrationTest {
    @Autowired
    private ProductService productService;

    @Nested
    @DisplayName("상품 리스트 조회")
    class GetProducts {
        @Test
        void 상품_리스트_조회_시_판매_중단_상품은_조회되지_않는다() {
            // given
            GetProductsQuery query = new GetProductsQuery(1L, 0, 100, "createdAt", "asc");

            // when
            PageResponse<ProductResult> result = productService.getProductsByCategory(query);

            // then
            assertThat(result.getContent())
                    .extracting(ProductResult::status)
                    .doesNotContain(SaleStatus.SUSPENDED);
        }

        @Test
        void 상품_리스트_조회_시_페이징_처리된다() {
            // given
            GetProductsQuery query = new GetProductsQuery(1L, 0, 4, "createdAt", "asc");

            // when
            PageResponse<ProductResult> result = productService.getProductsByCategory(query);

            // then
            assertThat(result.getPageInfo().getPage()).isEqualTo(query.page());
            assertThat(result.getPageInfo().getSize()).isEqualTo(query.size());
            assertThat(result.getContent()).hasSize(query.size());
        }

        @Test
        void 상품_리스트_조회_시_정렬_조건에_따라_정렬된다() {
            // given
            GetProductsQuery query = new GetProductsQuery(1L, 0, 100, "price", "desc");

            // when
            PageResponse<ProductResult> result = productService.getProductsByCategory(query);

            // then
            assertThat(result.getContent())
                    .extracting(ProductResult::price)
                    .isSortedAccordingTo(Comparator.reverseOrder());
        }

        @Test
        void 한글_데이터를_기준으로_정렬해도_정상적으로_정렬된다() {
            // given
            GetProductsQuery query = new GetProductsQuery(1L, 0, 100, "name", "asc");

            // when
            PageResponse<ProductResult> result = productService.getProductsByCategory(query);

            // then
            List<ProductResult> content = result.getContent();
            assertThat(content)
                    .isSortedAccordingTo(Comparator.comparing(ProductResult::name));
        }
    }
}
