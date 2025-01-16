package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.model.Stock;
import kr.hhplus.be.server.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.hhplus.be.server.fixture.unit.StockFixture.VALID_DEDUCTION_AMOUNT;
import static kr.hhplus.be.server.fixture.unit.StockFixture.stock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StockTest {
    @Nested
    @DisplayName("재고 차감")
    class DeductStockTest {
        @Test
        void 잔여_수량을_초과하여_차감할_때_예외가_발생한다() {
            // given
            Stock stock = stock();
            int quantity = stock.getQuantity();

            // when & then
            assertThatThrownBy(() -> stock.deduct(quantity + 1))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ProductErrorCode.PRODUCT_OUT_OF_STOCK.getMessage());
        }

        @Test
        void 재고를_차감할_때_잔여_수량이_차감한_만큼_감소한다() {
            // given
            Stock stock = stock();
            int amount = VALID_DEDUCTION_AMOUNT;
            int expectedQuantity = stock.getQuantity() - amount;

            // when
            stock.deduct(amount);

            // then
            int actualQuantity = stock.getQuantity();
            assertThat(actualQuantity).isEqualTo(expectedQuantity);
        }
    }
}
