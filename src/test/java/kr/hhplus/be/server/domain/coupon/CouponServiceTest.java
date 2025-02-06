package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.model.Coupon;
import kr.hhplus.be.server.domain.coupon.model.IssuedCoupon;
import kr.hhplus.be.server.global.exception.BusinessException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kr.hhplus.be.server.fixture.unit.CouponFixture.coupon;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {
    @InjectMocks
    private CouponService couponService;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private IssuedCouponRepository issuedCouponRepository;
    @Mock
    private CouponIssuanceManager couponIssuanceManager;

    @Nested
    class IssueCouponTest {
        @Test
        void 존재하지_않는_쿠폰_ID를_입력하면_쿠폰_발급에_실패한다() {
            // given
            long userId =1L;
            long nonExistentCouponId = 99L;
            IssueCouponCommand command = new IssueCouponCommand(nonExistentCouponId, userId);

            when(couponRepository.findById(command.couponId()))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> couponService.issue(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(CouponErrorCode.COUPON_NOT_FOUND.getMessage());
        }

        @Test
        void 쿠폰_개수_차감_후_잔여_수량이_0보다_작으면_쿠폰_발급에_실패한다() {
            // given
            long userId = 1L;
            Coupon coupon = coupon();
            IssueCouponCommand command = new IssueCouponCommand(coupon.getId(), userId);

            when(couponRepository.findById(command.couponId()))
                    .thenReturn(Optional.of(coupon));
            when(couponIssuanceManager.decreaseAndGetQuantity(coupon.getId()))
                    .thenReturn(-1);

            // when & then
            assertThatThrownBy(() -> couponService.issue(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(CouponErrorCode.COUPON_STOCK_DEPLETED.getMessage());
        }

        @Test
        void 사용자가_동일한_쿠폰을_발급_받은_경우_쿠폰_발급에_실패한다() {
            // given
            long userId = 1L;
            Coupon coupon = coupon();
            IssueCouponCommand command = new IssueCouponCommand(coupon.getId(), userId);

            when(couponRepository.findById(command.couponId()))
                    .thenReturn(Optional.of(coupon));
            when(couponIssuanceManager.decreaseAndGetQuantity(coupon.getId()))
                    .thenReturn(50);
            when(couponIssuanceManager.saveIssuedUser(coupon.getId(), userId))
                    .thenReturn(false);

            // when & then
            assertThatThrownBy(() -> couponService.issue(command))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(CouponErrorCode.COUPON_ALREADY_ISSUED.getMessage());
        }

        @Test
        void 쿠폰이_정상적으로_발급되면_발급된_쿠폰이_저장된다() {
            // given
            long userId = 1L;
            Coupon coupon = coupon();
            IssueCouponCommand command = new IssueCouponCommand(coupon.getId(), userId);
            IssuedCoupon expectedIssuedCoupon = new IssuedCoupon(coupon, userId);

            when(couponRepository.findById(command.couponId()))
                    .thenReturn(Optional.of(coupon));
            when(couponIssuanceManager.decreaseAndGetQuantity(coupon.getId()))
                    .thenReturn(50);
            when(couponIssuanceManager.saveIssuedUser(coupon.getId(), userId))
                    .thenReturn(true);
            when(issuedCouponRepository.save(any(IssuedCoupon.class)))
                    .thenReturn(expectedIssuedCoupon);

            // when
            couponService.issue(command);

            // then
            verify(issuedCouponRepository, times(1))
                    .save(argThat(issuedCoupon ->
                            issuedCoupon.getCoupon() == coupon
                            && issuedCoupon.getUserId() == userId
                    ));
        }
    }
}
