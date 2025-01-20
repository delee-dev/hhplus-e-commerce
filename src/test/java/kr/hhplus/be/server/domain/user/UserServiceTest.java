package kr.hhplus.be.server.domain.user;

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
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Nested
    @DisplayName("유저 조회")
    class GetUserTest {
        @Test
        void 존재하지_않는_회원을_조회할_때_예외가_발생한다() {
            // given
            long nonExistentUserId = 99L;

            when(userRepository.findById(nonExistentUserId))
                .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.getUser(nonExistentUserId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        }
    }
}
