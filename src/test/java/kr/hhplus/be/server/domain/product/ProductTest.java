package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import org.junit.jupiter.api.Test;

import static kr.hhplus.be.server.domain.product.fixture.ProductFixture.product;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductTest {
    @Test
    void 재고가_0이_되면_판매_상태는_TEMPORARILY_OUT으로_변경된다() {
        // given
        Product product = product();

        // when
        product.updateSaleStatus(0);

        // then
        assertThat(product.getStatus()).isEqualTo(SaleStatus.TEMPORARILY_OUT);
    }
}
