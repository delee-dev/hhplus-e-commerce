package kr.hhplus.be.server.fixture.unit;

import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import org.instancio.Instancio;
import org.instancio.Model;

import java.util.List;

import static org.instancio.Select.field;

public class ProductFixture {
    public static Category category() {
        return Instancio.of(Category.class)
                .set(field("id"), 1L)
                .set(field("name"), "전자기기")
                .create();
    }

    public static Category emptyCategory() {
        return Instancio.of(Category.class)
                .set(field("id"), 2L)
                .set(field("name"), "뷰티")
                .create();
    }

    private static final Model<Product> baseProduct = Instancio.of(Product.class)
            .set(field("id"), 1L)
            .set(field("name"), "무선이어폰")
            .set(field("description"), "고음질 무선이어폰")
            .set(field("category"), category())
            .set(field("price"), 100000L)
            .set(field("status"), SaleStatus.ON_SALE)
            .toModel();

    public static Product product() {
        return Instancio.of(baseProduct)
                .create();
    }

    public static List<Product> products() {
        return List.of(product());
    }

    public static List<Product> emptyProducts() {
        return List.of();
    }
}
