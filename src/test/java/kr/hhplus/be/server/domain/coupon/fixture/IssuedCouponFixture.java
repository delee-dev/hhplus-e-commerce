package kr.hhplus.be.server.domain.coupon.fixture;

import kr.hhplus.be.server.domain.coupon.model.Coupon;
import kr.hhplus.be.server.domain.coupon.model.CouponStatus;
import kr.hhplus.be.server.domain.coupon.model.IssuedCoupon;
import org.instancio.Instancio;
import org.instancio.InstancioApi;

import static org.instancio.Select.field;

public class IssuedCouponFixture {
    private static InstancioApi<IssuedCoupon> baseIssuedCoupon() {
        Coupon coupon = CouponFixture.coupon();

        return Instancio.of(IssuedCoupon.class)
                .set(field("id"), 1L)
                .set(field("coupon"), coupon)
                .set(field("userId"), 1L)
                .set(field("status"), CouponStatus.AVAILABLE)
                .set(field("used_at"), null);
    }

    public static IssuedCoupon issuedCoupon() {
        return baseIssuedCoupon()
                .create();
    }

    public static IssuedCoupon usedCoupon() {
        return baseIssuedCoupon()
                .set(field("status"), CouponStatus.USED)
                .create();

    }
}
