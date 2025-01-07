package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.model.Point;

import java.util.Optional;

public interface PointRepository {
    Optional<Point> findByUserId(Long userId);
    Optional<Point> findByUserIdWithLock(Long userId);
    Point save(Point point);
}
