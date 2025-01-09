package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.model.Coupon;
import kr.hhplus.be.server.global.exception.DomainException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kr.hhplus.be.server.fixture.TestDataFactory.CouponConstants.EXISTENT_COUPON_ID;
import static kr.hhplus.be.server.fixture.TestDataFactory.UserConstants.EXISTENT_USER_ID;
import static kr.hhplus.be.server.fixture.TestDataFactory.createCoupon;
import static kr.hhplus.be.server.fixture.TestDataFactory.createStockDepletedCoupon;
import static org.assertj.core.api.Assertions.assertThat;
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

    @Nested
    class IssueCouponTest {
        @Test
        void 존재하지_않는_쿠폰_ID를_입력하면_쿠폰_발급에_실패한다() {
            // given
            long couponId = EXISTENT_COUPON_ID;
            long userId = EXISTENT_USER_ID;
            IssueCouponCommand command = new IssueCouponCommand(couponId, userId);

            when(couponRepository.findByIdWithLock(command.couponId()))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> couponService.issueWithLock(command))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(CouponErrorCode.COUPON_NOT_FOUND.getMessage());
        }

        @Test
        void 쿠폰_잔여_수량이_없는_경우_쿠폰_발급에_실패한다() {
            // given
            Coupon coupon = createStockDepletedCoupon();
            long userId = EXISTENT_USER_ID;
            IssueCouponCommand command = new IssueCouponCommand(coupon.getId(), userId);

            when(couponRepository.findByIdWithLock(command.couponId()))
                    .thenReturn(Optional.of(coupon));

            // when & then
            assertThatThrownBy(() -> couponService.issueWithLock(command))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(CouponErrorCode.COUPON_STOCK_DEPLETED.getMessage());
        }

        @Test
        void 사용자가_동일한_쿠폰을_발급_받은_경우_쿠폰_발급에_실패한다() {
            // given
            Coupon coupon = createCoupon();
            long userId = EXISTENT_COUPON_ID;
            IssueCouponCommand command = new IssueCouponCommand(coupon.getId(), userId);

            when(couponRepository.findByIdWithLock(command.couponId()))
                    .thenReturn(Optional.of(coupon));
            when(issuedCouponRepository.existsByCouponIdAndUserId(command.couponId(), command.userId()))
                    .thenReturn(true);

            // when & then
            assertThatThrownBy(() -> couponService.issueWithLock(command))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(CouponErrorCode.COUPON_ALREADY_ISSUED.getMessage());
        }

        @Test
        void 쿠폰이_정상적으로_발급되면_쿠폰_잔여_수량이_1_차감된다() {
            // given
            Coupon coupon = createCoupon();
            long userId = EXISTENT_COUPON_ID;
            IssueCouponCommand command = new IssueCouponCommand(coupon.getId(), userId);

            int quantityBeforeIssue = coupon.getTotalQuantity();
            int expectedQuantity = quantityBeforeIssue - 1;

            when(couponRepository.findByIdWithLock(command.couponId()))
                    .thenReturn(Optional.of(coupon));
            when(issuedCouponRepository.existsByCouponIdAndUserId(command.couponId(), command.userId()))
                    .thenReturn(false);

            // when
            couponService.issueWithLock(command);

            // then
            assertThat(coupon.getTotalQuantity()).isEqualTo(expectedQuantity);
        }

        @Test
        void 쿠폰이_정상적으로_발급되면_발급된_쿠폰이_저장된다() {
            // given
            Coupon coupon = createCoupon();
            long userId = EXISTENT_COUPON_ID;

            IssueCouponCommand command = new IssueCouponCommand(coupon.getId(), userId);

            when(couponRepository.findByIdWithLock(command.couponId()))
                    .thenReturn(Optional.of(coupon));
            when(issuedCouponRepository.existsByCouponIdAndUserId(command.couponId(), command.userId()))
                    .thenReturn(false);

            // when
            couponService.issueWithLock(command);

            // then
            verify(issuedCouponRepository, times(1))
                    .save(argThat(issuedCoupon ->
                            issuedCoupon.getCoupon() == coupon
                            && issuedCoupon.getUserId() == userId
                    ));
        }
    }
}
