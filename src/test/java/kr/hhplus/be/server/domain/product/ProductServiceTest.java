package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.GetProductsQuery;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.fixture.TestDataFactory;
import kr.hhplus.be.server.global.model.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;

    @Nested
    @DisplayName("상품 리스트 조회")
    class GetProductsTest {
        @Test
        void 카테고리별_상품_조회_시_판매_중단_상품은_조회되지_않는다() {
            // given
            Category category = TestDataFactory.createCategory();
            List<Product> products = TestDataFactory.createProductList();

            GetProductsQuery query = new GetProductsQuery(category.getId(), 0, 10, "createdAt", "asc");
            PageResponse<Product> productPage = new PageResponse<>(products, new PageResponse.PageInfo(query.page(), query.page(), products.size(), 0));

            when(productRepository.findProductsByCategoryIdAndStatusNot(query.categoryId(), SaleStatus.SUSPENDED, query.page(), query.size(), query.sortColumn(), query.sortDirection()))
                    .thenReturn(productPage);

            // when
            PageResponse<ProductResult> result = productService.getProductsByCategory(query);

            // then
            assertThat(result.getContent())
                    .extracting(ProductResult::status)
                    .doesNotContain(SaleStatus.SUSPENDED);
        }

        @Test
        void 상품이_등록되지_않은_카테고리를_검색하면_빈_리스트를_반환한다() {
            // given
            Category emptyCategory = TestDataFactory.createEmptyCategory();
            List<Product> emptyProducts = List.of();

            GetProductsQuery query = new GetProductsQuery(emptyCategory.getId(), 0, 10, "createdAt", "asc");
            PageResponse<Product> productPage = new PageResponse<>(emptyProducts, new PageResponse.PageInfo(query.page(), query.page(), emptyProducts.size(), 0));

            when(productRepository.findProductsByCategoryIdAndStatusNot(query.categoryId(), SaleStatus.SUSPENDED, query.page(), query.size(), query.sortColumn(), query.sortDirection()))
                    .thenReturn(productPage);

            // when
            PageResponse<ProductResult> result = productService.getProductsByCategory(query);

            // then
            assertThat(result.getContent()).hasSize(0);
        }
    }
}
