package kr.hhplus.be.server.fixture.unit;

import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.Stock;
import org.instancio.Instancio;
import org.instancio.InstancioApi;

import static kr.hhplus.be.server.fixture.unit.ProductFixture.product;
import static org.instancio.Select.field;

public class StockFixture {
    public static int VALID_DEDUCTION_AMOUNT = 5;

    private static InstancioApi<Stock> baseStock() {
        Product product = product();

        return Instancio.of(Stock.class)
                .set(field("id"), 1L)
                .set(field("product"), product)
                .set(field("quantity"), 100);
    }

    public static Stock stock() {
        return baseStock()
                .create();
    }
}
