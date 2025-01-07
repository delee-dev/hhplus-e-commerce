package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.global.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DomainException(UserErrorCode.USER_NOT_FOUND));
    }
}
