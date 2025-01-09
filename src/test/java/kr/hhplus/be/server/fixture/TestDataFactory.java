package kr.hhplus.be.server.fixture;

import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.domain.user.model.User;
import org.instancio.Instancio;

import java.util.List;

import static org.instancio.Select.field;

public class TestDataFactory {
    public static class UserConstants {
        public static final long NON_EXISTENT_USER_ID = 999;
        public static final long EXISTENT_USER_ID = 1;
    }

    public static class PointConstants {
        public static final long VALID_CHARGE_AMOUNT = 10_000;
        public static final long BELOW_MIN_CHARGE_AMOUNT = 900;
        public static final long EXCEED_MAX_CHARGE_AMOUNT = 1_500_000;
        public static final long VALID_USE_AMOUNT = 10_000L;
        public static final long EXCEED_BALANCE_AMOUNT = 60_000L;
    }

    public static User createUser() {
        return Instancio.of(User.class)
                .set(field("id"), 1L)
                .set(field("name"), "김철수")
                .create();
    }

    public static Point createPoint() {
        User user = createUser();
        return Instancio.of(Point.class)
                .set(field("id"), 1L)
                .set(field("balance"), 50_000L)
                .set(field("userId"), user.getId())
                .set(field("version"), 1L)
                .create();
    }

    public static Point createNearLimitPoint() {
        User user = createUser();
        return Instancio.of(Point.class)
                .set(field("id"), 1L)
                .set(field("balance"), 9_999_999L)
                .set(field("userId"), user.getId())
                .set(field("version"), 1L)
                .create();
    }

    public static Category createCategory() {
        return Instancio.of(Category.class)
                .set(field("id"), 1L)
                .set(field("name"), "전자기기")
                .create();
    }

    public static Category createEmptyCategory() {
        return Instancio.of(Category.class)
                .set(field("id"), 9L)
                .set(field("name"), "뷰티")
                .create();
    }

    public static List<Product> createProductList() {
        Category category = createCategory();
        return List.of(
                Instancio.of(Product.class)
                        .set(field("id"), 1L)
                        .set(field("name"), "무선이어폰")
                        .set(field("description"), "고음질 무선이어폰")
                        .set(field("category"), category)
                        .set(field("price"), 100000L)
                        .set(field("status"), SaleStatus.ON_SALE)
                        .create(),
                Instancio.of(Product.class)
                        .set(field("id"), 2L)
                        .set(field("name"), "태블릿PC")
                        .set(field("description"), "가성비 태블릿")
                        .set(field("category"), category)
                        .set(field("price"), 200000L)
                        .set(field("status"), SaleStatus.ON_SALE)
                        .create()
        );
    }
}
