package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(long id);
}
