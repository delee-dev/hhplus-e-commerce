package kr.hhplus.be.server.fixture.unit;

import kr.hhplus.be.server.domain.coupon.model.Coupon;
import kr.hhplus.be.server.domain.coupon.model.DiscountType;
import org.instancio.Instancio;
import org.instancio.Model;

import java.time.LocalDateTime;

import static org.instancio.Select.field;

public class CouponFixture {
    public static final long VALID_ORDER_AMOUNT = 150_000L;
    public static final long AMOUNT_EXCEEDING_MAX_DISCOUNT = 200_000L;

    private static final Model<Coupon> baseCoupon = Instancio.of(Coupon.class)
            .set(field("id"), 1L)
            .set(field("name"), "회원가입 쿠폰")
            .set(field("discountType"), DiscountType.FIXED_AMOUNT)
            .set(field("discountAmount"), 5_000L)
            .set(field("minOrderAmount"), 10_000L)
            .set(field("maxDiscountAmount"), null)
            .set(field("validFrom"), LocalDateTime.now().minusMonths(1))
            .set(field("validUntil"), LocalDateTime.now().plusMonths(1))
            .set(field("totalQuantity"), 100)
            .toModel();

    public static Coupon coupon() {
        return Instancio.of(baseCoupon)
                .create();
    }

    public static Coupon couponWithoutStock() {
        return Instancio.of(baseCoupon)
                .set(field("totalQuantity"), 0)
                .create();
    }

    public static Coupon expiredCoupon() {
        return Instancio.of(baseCoupon)
                .set(field("minOrderAmount"), 100_000L)
                .set(field("validFrom"), LocalDateTime.now().minusMonths(2))
                .set(field("validUntil"), LocalDateTime.now().minusMonths(1))
                .create();
    }

    public static Coupon fixedAmountCoupon() {
        return coupon();
    }

    public static Coupon fiveThousandDiscountCoupon() {
        return coupon();
    }

    public static Coupon percentageCoupon() {
        return Instancio.of(baseCoupon)
                .set(field("discountType"), DiscountType.PERCENTAGE)
                .set(field("discountAmount"), 20L)
                .set(field("maxDiscountAmount"), null)
                .create();
    }

    public static Coupon couponWithDiscountLimit() {
        return Instancio.of(baseCoupon)
                .set(field("discountType"), DiscountType.PERCENTAGE)
                .set(field("discountAmount"), 20L)
                .set(field("maxDiscountAmount"), 10_000L)
                .create();
    }
}
