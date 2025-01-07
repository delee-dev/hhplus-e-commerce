package kr.hhplus.be.server.infrastructure.point.persistence;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.point.model.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointJpaRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUserId(Long userId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select p from Point p where p.userId = :userId")
    Optional<Point> findByUserIdWithLock(@Param("userId") Long userId);
}
