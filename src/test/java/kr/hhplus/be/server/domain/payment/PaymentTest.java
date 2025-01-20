package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static kr.hhplus.be.server.fixture.unit.PaymentFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class PaymentTest {
    @Nested
    @DisplayName("결제 검증")
    class ValidatePaymentTest {
        @Test
        void 완료된_결제는_검증시_예외가_발생한다() {
            // given
            Payment completedPayment = completedPayment();

            // when & then
            assertThatThrownBy(() -> completedPayment.validatePaymentStatus())
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(PaymentErrorCode.PAYMENT_ALREADY_COMPLETED.getMessage());

        }

        @Test
        void 취소된_결제는_검증시_예외가_발생한다() {
            // given
            Payment canceledPayment = canceledPayment();

            // when & then
            assertThatThrownBy(() -> canceledPayment.validatePaymentStatus())
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(PaymentErrorCode.PAYMENT_ALREADY_CANCELLED.getMessage());
        }
    }

    @Nested
    @DisplayName("할인 금액 적용")
    class ApplyDiscountTest {
        @Test
        void 할인을_적용하면_최종_금액이_할인_금액만큼_차감된다() {
            // given
            Payment payment = payment();
            long amountBeforeApplyDiscount = payment.getTotalAmount();
            long discountAmount = 10_000L;
            long expectedFinalAmount = amountBeforeApplyDiscount - discountAmount;
            // when
            payment.applyDiscount(discountAmount);

            // then
            long actualFinalAmount = payment.getFinalAmount();
            assertThat(actualFinalAmount).isEqualTo(expectedFinalAmount);
        }
    }
}
