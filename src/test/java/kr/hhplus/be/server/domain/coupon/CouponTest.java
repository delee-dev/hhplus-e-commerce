package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.model.Coupon;
import kr.hhplus.be.server.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.hhplus.be.server.fixture.unit.CouponFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CouponTest {
    @Nested
    @DisplayName("쿠폰 사용 검증")
    class UseCouponTest {
        @Test
        void 유효_기간이_만료된_쿠폰은_사용에_실패한다() {
            // given
            Coupon expiredCoupon = expiredCoupon();

            // when & then
            assertThatThrownBy(() -> expiredCoupon.validateUsage(VALID_ORDER_AMOUNT))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(CouponErrorCode.COUPON_INVALID.getMessage());
        }

        @Test
        void 주문금액이_최소금액_미만이면_사용에_실패한다() {
            // given
            Coupon coupon = coupon();
            Long belowMinOrderAmount = coupon.getMinOrderAmount() - 1_000L;

            // when & then
            assertThatThrownBy(() -> coupon.validateUsage(belowMinOrderAmount))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(CouponErrorCode.COUPON_NOT_APPLICABLE_TO_PAYMENT.getMessage());
        }
    }

    @Nested
    @DisplayName("쿠폰 할인 금액 계산")
    class CalculateCouponTest {
        @Test
        void 정액_쿠폰_할인_금액_계산시_정해진_금액을_반환한다() {
            // given
            Coupon fixedAmountCoupon = fixedAmountCoupon();
            Long discountAmount = fixedAmountCoupon.getDiscountAmount();

            // when
            Long actualDiscountAmount = fixedAmountCoupon.calculateDiscountAmount(VALID_ORDER_AMOUNT);

            // then
            assertThat(actualDiscountAmount).isEqualTo(discountAmount);
        }

        @Test
        void 할인_금액이_주문_금액을_초과하는_경우_주문_금액을_반환한다() {
            // given
            Long amount = 4_000L;
            Coupon fiveThousandDiscountCoupon = fiveThousandDiscountCoupon();

            // when
            long actualDiscountAmount = fiveThousandDiscountCoupon.calculateDiscountAmount(amount);

            // then
            assertThat(actualDiscountAmount).isEqualTo(amount);
        }

        @Test
        void 정률_쿠폰_할인_금액_계산시_주문_금액에_할인_비율을_곱한_값을_반환한다() {
            // given
            Coupon percentageCoupon = percentageCoupon();

            Long amount = VALID_ORDER_AMOUNT;
            Long discountPercent = percentageCoupon.getDiscountAmount();
            Long expectedDiscountAmount = (amount * discountPercent) / 100;


            // when
            long actualDiscountAmount = percentageCoupon.calculateDiscountAmount(amount);

            // then
            assertThat(actualDiscountAmount).isEqualTo(expectedDiscountAmount);
        }

        @Test
        void 최대_할인_금액이_있는_경우_최대_할인_금액을_초과할_수_없다() {
            // given
            Coupon couponWithDiscountLimit = couponWithDiscountLimit();

            long amount = AMOUNT_EXCEEDING_MAX_DISCOUNT;
            long discountPercent = couponWithDiscountLimit.getDiscountAmount();
            long maxDiscountAmount = couponWithDiscountLimit.getMaxDiscountAmount();
            long expectedDiscountAmount = Math.min((amount * discountPercent) / 100, maxDiscountAmount);

            // when
            long actualDiscountAmount = couponWithDiscountLimit.calculateDiscountAmount(amount);

            // then
            assertThat(actualDiscountAmount).isEqualTo(expectedDiscountAmount);
        }
    }
}
