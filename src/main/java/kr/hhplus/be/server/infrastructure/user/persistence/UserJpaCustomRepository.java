package kr.hhplus.be.server.infrastructure.user.persistence;

import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserJpaCustomRepository implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(long id) {
        return userJpaRepository.findById(id);
    }
}
