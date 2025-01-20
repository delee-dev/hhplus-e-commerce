package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.hhplus.be.server.fixture.unit.PointFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PointTest {
    @Nested
    @DisplayName("포인트 충전")
    class ChargePointTest {
        @Test
        void 충전_금액이_1회_최소_충전_금액에_미달하면_충전에_실패한다() {
            // given
            Point point = point();

            // when & then
            assertThatThrownBy(() -> point.charge(BELOW_MIN_CHARGE_AMOUNT))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(PointErrorCode.POINT_CHARGE_BELOW_MINIMUM.getMessage());
        }

        @Test
        void 충전_금액이_1회_최대_충전_금액을_초과하면_충전에_실패한다() {
            // given
            Point point = point();

            // when & then
            assertThatThrownBy(() -> point.charge(EXCEED_MAX_CHARGE_AMOUNT))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(PointErrorCode.POINT_CHARGE_EXCEEDS_MAXIMUM.getMessage());
        }

        @Test
        void 충전_후_잔액이_한도를_초과하면_충전에_실패한다() {
            // given
            Point pointNearLimit = pointNearLimit();

            // when & then
            assertThatThrownBy(() -> pointNearLimit.charge(VALID_CHARGE_AMOUNT))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(PointErrorCode.POINT_BALANCE_EXCEEDS_LIMIT.getMessage());
        }

        @Test
        void 충전_성공시_잔액이_충전금액만큼_증가한다() {
            // given
            Point point = point();
            long balanceBeforeCharge = point.getBalance();
            long chargeAmount = VALID_CHARGE_AMOUNT;
            long expectedBalance = balanceBeforeCharge + chargeAmount;

            // when
            point.charge(chargeAmount);

            // then
            Long actualBalance = point.getBalance();
            assertThat(actualBalance).isEqualTo(expectedBalance);
        }
    }

    @Nested
    @DisplayName("포인트 사용")
    class UsePointTest {
        @Test
        void 잔액보다_큰_금액은_사용할_수_없다() {
            // given
            Point point = point();
            Long exceedBalance = point.getBalance() + 1_000L;

            // when & then
            assertThatThrownBy(() -> point.use(exceedBalance))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(PointErrorCode.POINT_BALANCE_INSUFFICIENT.getMessage());
        }

        @Test
        void 사용_성공시_잔액이_사용금액만큼_감소한다() {
            // given
            Point point = point();

            long balanceBeforeUse = point.getBalance();
            long useAmount = VALID_USE_AMOUNT;
            long expectedBalance = balanceBeforeUse - useAmount;

            // when
            point.use(useAmount);

            // then
            Long actualBalance = point.getBalance();
            assertThat(actualBalance).isEqualTo(expectedBalance);
        }
    }
}
