package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.model.CouponStatus;
import kr.hhplus.be.server.domain.coupon.model.IssuedCoupon;
import kr.hhplus.be.server.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.hhplus.be.server.fixture.unit.CouponFixture.VALID_ORDER_AMOUNT;
import static kr.hhplus.be.server.fixture.unit.IssuedCouponFixture.issuedCoupon;
import static kr.hhplus.be.server.fixture.unit.IssuedCouponFixture.usedCoupon;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class IssuedCouponTest {
    @Nested
    @DisplayName("쿠폰 사용")
    class UseCouponTest {
        @Test
        void 이미_사용한_쿠폰은_사용에_실패한다() {
            // given
            IssuedCoupon usedCoupon = usedCoupon();

            // when & then
            assertThatThrownBy(() -> usedCoupon.use(VALID_ORDER_AMOUNT))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(CouponErrorCode.COUPON_ALREADY_USED.getMessage());
        }

        @Test
        void 쿠폰_사용에_성공하면_쿠폰의_상태가_변경된다() {
            // given
            IssuedCoupon issuedCoupon = issuedCoupon();

            // when
            issuedCoupon.use(VALID_ORDER_AMOUNT);

            // then
            assertThat(issuedCoupon.getStatus()).isEqualTo(CouponStatus.USED);
            assertThat(issuedCoupon.getUsed_at()).isNotNull();
        }
    }
}
