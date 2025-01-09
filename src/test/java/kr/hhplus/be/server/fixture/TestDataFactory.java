package kr.hhplus.be.server.fixture;

import kr.hhplus.be.server.domain.coupon.model.Coupon;
import kr.hhplus.be.server.domain.coupon.model.CouponStatus;
import kr.hhplus.be.server.domain.coupon.model.DiscountType;
import kr.hhplus.be.server.domain.coupon.model.IssuedCoupon;
import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.domain.product.model.Stock;
import kr.hhplus.be.server.domain.user.model.User;
import org.instancio.Instancio;

import java.time.LocalDateTime;
import java.util.List;

import static org.instancio.Select.field;

public class TestDataFactory {
    public static class UserConstants {
        public static final long NON_EXISTENT_USER_ID = 999L;
        public static final long EXISTENT_USER_ID = 1L;
    }

    public static class PointConstants {
        public static final long VALID_CHARGE_AMOUNT = 10_000;
        public static final long BELOW_MIN_CHARGE_AMOUNT = 900;
        public static final long EXCEED_MAX_CHARGE_AMOUNT = 1_500_000;
        public static final long VALID_USE_AMOUNT = 10_000;
        public static final long EXCEED_BALANCE_AMOUNT = 60_000;
    }

    public static class StockConstants {
        public static final int EXCEED_CURRENT_STOCK_QUANTITY = 11;
        public static final int CURRENT_STOCK_QUANTITY = 5;
    }

    public static class CouponConstants {
        public static final long NON_EXISTENT_COUPON_ID = 999L;
        public static final long EXISTENT_COUPON_ID = 1L;
        public static final long VALID_ORDER_AMOUNT = 150_000L;
        public static final long BELOW_MIN_COUPON_AMOUNT = 1_500L;
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

    public static List<Product> createProducts() {
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

    public static List<Stock> createStocks() {
        List<Product> products = createProducts();

        return List.of(
                Instancio.of(Stock.class)
                        .set(field("id"), 1L)
                        .set(field("product"), products.get(0))
                        .set(field("quantity"), 5)
                        .create(),
                Instancio.of(Stock.class)
                        .set(field("id"), 2L)
                        .set(field("product"), products.get(1))
                        .set(field("quantity"), 5)
                        .create()
        );
    }

    public static Coupon createCoupon() {
        return Instancio.of(Coupon.class)
                .set(field("id"), 1L)
                .set(field("name"), "신규가입 할인")
                .set(field("discountType"), DiscountType.FIXED_AMOUNT)
                .set(field("discountAmount"), 10000L)
                .set(field("minOrderAmount"), 50000L)
                .set(field("maxDiscountAmount"), 10000L)
                .set(field("validFrom"), LocalDateTime.now().minusMonths(1))
                .set(field("validUntil"), LocalDateTime.now().plusMonths(1))
                .set(field("totalQuantity"), 100)
                .create();
    }

    public static Coupon createStockDepletedCoupon() {
        return Instancio.of(Coupon.class)
                .set(field("id"), 2L)
                .set(field("name"), "겨울 시즌 할인")
                .set(field("discountType"), DiscountType.PERCENTAGE)
                .set(field("discountAmount"), 10L)
                .set(field("minOrderAmount"), 100000L)
                .set(field("maxDiscountAmount"), 20000L)
                .set(field("validFrom"), LocalDateTime.now().minusMonths(1))
                .set(field("validUntil"), LocalDateTime.now().plusMonths(1))
                .set(field("totalQuantity"), 0)
                .create();
    }

    public static Coupon createExpiredCoupon() {
        return Instancio.of(Coupon.class)
                .set(field("id"), 3L)
                .set(field("name"), "여름 시즌 할인")
                .set(field("discountType"), DiscountType.PERCENTAGE)
                .set(field("discountAmount"), 10L)
                .set(field("minOrderAmount"), 100000L)
                .set(field("maxDiscountAmount"), 20000L)
                .set(field("validFrom"), LocalDateTime.now().minusMonths(2))
                .set(field("validUntil"), LocalDateTime.now().minusMonths(1))
                .set(field("totalQuantity"), 0)
                .create();
    }

    public static IssuedCoupon createIssuedCoupon() {
        Coupon coupon = createCoupon();

        return Instancio.of(IssuedCoupon.class)
                .set(field("id"), 1L)
                .set(field("coupon"), coupon)
                .set(field("userId"), 1L)
                .set(field("status"), CouponStatus.AVAILABLE)
                .set(field("used_at"), null)
                .create();
    }

    public static IssuedCoupon createUsedIssuedCoupon() {
        Coupon coupon = createCoupon();

        return Instancio.of(IssuedCoupon.class)
                .set(field("id"), 2L)
                .set(field("coupon"), coupon)
                .set(field("userId"), 2L)
                .set(field("status"), CouponStatus.USED)
                .set(field("used_at"), LocalDateTime.now().minusDays(3))
                .create();
    }

    public static IssuedCoupon createExpiredIssuedCoupon() {
        Coupon coupon = createExpiredCoupon();

        return Instancio.of(IssuedCoupon.class)
                .set(field("id"), 3L)
                .set(field("coupon"), coupon)
                .set(field("userId"), 3L)
                .set(field("status"), CouponStatus.AVAILABLE)
                .set(field("used_at"), null)
                .create();
    }
}
