package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.point.model.TransactionType;
import kr.hhplus.be.server.fixture.TestDataFactory;
import kr.hhplus.be.server.global.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
    @InjectMocks
    private PointService pointService;
    @Mock
    private PointRepository pointRepository;
    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Nested
    @DisplayName("포인트 조회")
    class GetPointTest {
        @Test
        void 존재하지_않는_회원_ID를_입력하면_조회에_실패한다() {
            // given
            long userId = TestDataFactory.UserConstants.NON_EXISTENT_USER_ID;
            when(pointRepository.findByUserId(userId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> pointService.getPoint(userId))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(PointErrorCode.POINT_BALANCE_NOT_FOUND.getMessage());
        }

        @Test
        void 존재하는_회원_ID를_입력하면_성공적으로_조회된다() {
            // given
            Point point = TestDataFactory.createPoint();
            long userId = point.getUserId();

            when(pointRepository.findByUserId(userId))
                    .thenReturn(Optional.of(point));

            // when
            Point actual = pointService.getPoint(userId);

            // then
            assertThat(actual.getUserId()).isEqualTo(point.getUserId());
            assertThat(actual.getId()).isEqualTo(point.getId());
            assertThat(actual.getBalance()).isEqualTo(point.getBalance());
        }
    }

    @Nested
    @DisplayName("포인트 충전")
    class ChargePointTest {
        @Test
        void 존재하지_않는_회원_ID를_입력하면_충전에_실패한다() {
            // given
            long nonExistentUserId = TestDataFactory.UserConstants.NON_EXISTENT_USER_ID;
            long chargeAmount = TestDataFactory.PointConstants.VALID_CHARGE_AMOUNT;

            when(pointRepository.findByUserIdWithLock(nonExistentUserId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> pointService.chargeWithLock(nonExistentUserId, chargeAmount))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(PointErrorCode.POINT_BALANCE_NOT_FOUND.getMessage());
        }

        @Test
        void 충전_금액이_1회_최소_충전_금액에_미달하면_충전에_실패한다() {
            // given
            Point point = TestDataFactory.createPoint();
            long userId = point.getUserId();
            long belowMinChargeAmount = TestDataFactory.PointConstants.BELOW_MIN_CHARGE_AMOUNT;

            when(pointRepository.findByUserIdWithLock(userId))
                    .thenReturn(Optional.of(point));

            // when & then
            assertThatThrownBy(() -> pointService.chargeWithLock(userId, belowMinChargeAmount))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(PointErrorCode.POINT_CHARGE_BELOW_MINIMUM.getMessage());
        }

        @Test
        void 충전_금액이_1회_최대_충전_금액을_초과하면_충전에_실패한다() {
            // given
            Point point = TestDataFactory.createPoint();
            long userId = point.getUserId();
            long exceedMaxChargeAmount = TestDataFactory.PointConstants.EXCEED_MAX_CHARGE_AMOUNT;

            when(pointRepository.findByUserIdWithLock(userId))
                    .thenReturn(Optional.of(point));

            // when & then
            assertThatThrownBy(() -> pointService.chargeWithLock(userId, exceedMaxChargeAmount))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(PointErrorCode.POINT_CHARGE_EXCEEDS_MAXIMUM.getMessage());
        }

        @Test
        void 충전_후_잔액이_보유_한도를_초과하면_충전예_실패한다() {
            // given
            Point nearLimitPoint = TestDataFactory.createNearLimitPoint();
            long userId = nearLimitPoint.getUserId();
            long validChargeAmount = TestDataFactory.PointConstants.VALID_CHARGE_AMOUNT;

            when(pointRepository.findByUserIdWithLock(userId))
                    .thenReturn(Optional.of(nearLimitPoint));

            // when & then
            assertThatThrownBy(() -> pointService.chargeWithLock(userId, validChargeAmount))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(PointErrorCode.POINT_BALANCE_EXCEEDS_LIMIT.getMessage());
        }

        @Test
        void 충전_성공_후_잔액은_충전_전_잔액에_충전_금액을_더한_값과_같다() {
            // given
            Point point = TestDataFactory.createPoint();
            long userId = point.getUserId();

            long balanceBeforeCharge = point.getBalance();
            long chargeAmount = TestDataFactory.PointConstants.VALID_CHARGE_AMOUNT;
            long expectedBalance = balanceBeforeCharge + chargeAmount;

            when(pointRepository.findByUserIdWithLock(userId))
                    .thenReturn(Optional.of(point));

            // when
            Point actual = pointService.chargeWithLock(userId, chargeAmount);

            // then
            assertThat(actual.getBalance()).isEqualTo(expectedBalance);
        }

        @Test
        void 충전_성공_후_포인트_충전_내역이_저장된다() {
            // given
            Point point = TestDataFactory.createPoint();
            long userId = point.getUserId();
            long chargeAmount = TestDataFactory.PointConstants.VALID_CHARGE_AMOUNT;

            when(pointRepository.findByUserIdWithLock(userId))
                    .thenReturn(Optional.of(point));

            // when
            pointService.chargeWithLock(userId, chargeAmount);

            // then
            verify(pointHistoryRepository, times(1))
                    .save(argThat(pointHistory ->
                            pointHistory.getPoint() == point &&
                            pointHistory.getAmount() == chargeAmount &&
                            pointHistory.getType() == TransactionType.CHARGE
                    ));
        }
    }

    @Nested
    @DisplayName("포인트 사용")
    class UsePointTest {
        @Test
        void 존재하지_않는_회원_ID를_입력하면_사용에_실패한다() {
            // given
            long nonExistentUserId = TestDataFactory.UserConstants.NON_EXISTENT_USER_ID;
            long useAmount = TestDataFactory.PointConstants.VALID_USE_AMOUNT;

            when(pointRepository.findByUserIdWithLock(nonExistentUserId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> pointService.useWithLock(nonExistentUserId, useAmount))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(PointErrorCode.POINT_BALANCE_NOT_FOUND.getMessage());
        }

        @Test
        void 사용_금액이_잔액을_초과하면_사용에_실패한다() {
            // given
            long userId = TestDataFactory.UserConstants.EXISTENT_USER_ID;
            long exceedBalanceAmount = TestDataFactory.PointConstants.EXCEED_BALANCE_AMOUNT;
            Point point = TestDataFactory.createPoint();

            when(pointRepository.findByUserIdWithLock(userId))
                    .thenReturn(Optional.of(point));

            // when & then
            assertThatThrownBy(() -> pointService.useWithLock(userId, exceedBalanceAmount))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(PointErrorCode.POINT_BALANCE_INSUFFICIENT.getMessage());
        }

        @Test
        void 사용_성공_후_잔액은_사용_전_잔액에_사용_금액을_뺀_값과_같다() {
            // given
            Point point = TestDataFactory.createPoint();
            long userId = point.getUserId();

            long balanceBeforeUse = point.getBalance();
            long useAmount = TestDataFactory.PointConstants.VALID_USE_AMOUNT;
            long expectedBalance = balanceBeforeUse - useAmount;

            when(pointRepository.findByUserIdWithLock(userId))
                    .thenReturn(Optional.of(point));

            // when
            Point actual = pointService.useWithLock(userId, useAmount);

            // then
            assertThat(actual.getBalance()).isEqualTo(expectedBalance);
        }

        @Test
        void 사용_성공_후_포인트_사용_내역이_저장된다() {
            // given
            Point point = TestDataFactory.createPoint();
            long userId = point.getUserId();
            long useAmount = TestDataFactory.PointConstants.VALID_USE_AMOUNT;

            when(pointRepository.findByUserIdWithLock(userId))
                    .thenReturn(Optional.of(point));

            // when
            pointService.useWithLock(userId, useAmount);

            // then
            verify(pointHistoryRepository, times(1))
                    .save(argThat(pointHistory ->
                            pointHistory.getPoint() == point &&
                            pointHistory.getAmount() == useAmount &&
                            pointHistory.getType() == TransactionType.USE
                    ));
        }
    }

}
