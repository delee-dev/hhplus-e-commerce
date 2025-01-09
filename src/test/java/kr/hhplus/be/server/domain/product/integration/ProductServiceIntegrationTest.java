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
public class ProductServiceIntegrationTest {
    @Autowired
    private ProductService productService;

    @Nested
    @Sql("list_data.sql")
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

    @Nested
    @Sql("best_data.sql")
    @DisplayName("상위 판매 상품 리스트 조회")
    class GetBestSellingProducts {
        @Test
        void 상위_판매_상품_리스트_조회() {
            // given
            long categoryId = 1;

            // when
            List<ProductResult> result = productService.getBestSellingProducts(categoryId);

            // then
            assertThat(result.get(0))
                    .extracting("id", "name")
                    .contains(1L, "무선이어폰");

            assertThat(result.get(1))
                    .extracting("id", "name")
                    .contains(7L, "게이밍 마우스");

            assertThat(result.get(2))
                    .extracting("id", "name")
                    .contains(5L, "스마트워치");

            assertThat(result.get(3))
                    .extracting("id", "name")
                    .contains(2L, "태블릿PC");

            assertThat(result.get(4))
                    .extracting("id", "name")
                    .contains(8L, "기계식 키보드");
        }
    }
}
