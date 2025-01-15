package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.point.model.TransactionType;
import kr.hhplus.be.server.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kr.hhplus.be.server.domain.point.fixture.PointFixture.*;
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
        void 존재하지_않는_회원을_조회할_때_예외가_발생한다() {
            // given
            long nonExistentUserId = 99L;

            when(pointRepository.findByUserId(nonExistentUserId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> pointService.getPoint(nonExistentUserId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(PointErrorCode.POINT_BALANCE_NOT_FOUND.getMessage());
        }

        @Test
        void 존재하는_회원을_조회할_때_포인트_정보가_반환된다() {
            // given
            Point point = point();
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
        void 존재하지_않는_회원이_충전할_때_예외가_발생한다() {
            // given
            long nonExistentUserId = 99L;
            long chargeAmount = VALID_CHARGE_AMOUNT;

            when(pointRepository.findByUserIdWithLock(nonExistentUserId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> pointService.chargeWithLock(nonExistentUserId, chargeAmount))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(PointErrorCode.POINT_BALANCE_NOT_FOUND.getMessage());
        }

        @Test
        void 포인트_충전_성공_후_충전_내역이_저장된다() {
            // given
            Point point = point();
            long userId = point.getUserId();
            long chargeAmount = VALID_CHARGE_AMOUNT;

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
        void 존재하지_않는_회원이_사용할_때_예외가_발생한다() {
            // given
            long nonExistentUserId = 99L;
            long useAmount = VALID_USE_AMOUNT;

            when(pointRepository.findByUserIdWithLock(nonExistentUserId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> pointService.useWithLock(nonExistentUserId, useAmount))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(PointErrorCode.POINT_BALANCE_NOT_FOUND.getMessage());
        }

        @Test
        void 포인트_사용_성공_후_사용_내역이_저장된다() {
            // given
            Point point = point();
            long userId = point.getUserId();
            long useAmount = VALID_USE_AMOUNT;

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
