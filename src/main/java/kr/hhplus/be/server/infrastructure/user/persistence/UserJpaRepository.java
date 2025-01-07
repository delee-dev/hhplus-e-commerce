package kr.hhplus.be.server.infrastructure.user.persistence;

import kr.hhplus.be.server.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserJpaRepository extends JpaRepository<User, Long> {
}
