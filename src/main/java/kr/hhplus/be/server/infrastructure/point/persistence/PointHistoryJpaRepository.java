package kr.hhplus.be.server.infrastructure.point.persistence;

import kr.hhplus.be.server.domain.point.model.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {
}
