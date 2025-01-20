package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;

    @Nested
    @DisplayName("결제 완료")
    class CompletePaymentTest {
        @Test
        void 주문_ID가_존재하지_않는_경우_완료_처리에_실패한다() {
            // given
            Long nonExistentOrderId = 99L;

            when(orderRepository.findById(nonExistentOrderId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.completePayment(nonExistentOrderId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(OrderErrorCode.ORDER_NOT_FOUND.getMessage());
        }
    }
}
