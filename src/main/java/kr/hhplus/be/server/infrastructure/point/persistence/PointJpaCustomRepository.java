package kr.hhplus.be.server.infrastructure.point.persistence;

import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.model.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointJpaCustomRepository implements PointRepository {
    private final PointJpaRepository pointJpaRepository;

    @Override
    public Optional<Point> findByUserId(Long userId) {
        return pointJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<Point> findByUserIdWithLock(Long userId) {
        return pointJpaRepository.findByUserIdWithLock(userId);
    }

    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }
}
