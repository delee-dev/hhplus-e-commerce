package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.point.model.PointHistory;
import kr.hhplus.be.server.domain.point.model.TransactionType;
import kr.hhplus.be.server.global.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public Point getPoint(Long userId) {
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new DomainException(PointErrorCode.POINT_BALANCE_NOT_FOUND));
    }

    public Point chargeWithLock(Long userId, Long amount) {
        // TODO: 재시도 로직 추가
        Point point = pointRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new DomainException(PointErrorCode.POINT_BALANCE_NOT_FOUND));
        point.charge(amount);
        pointRepository.save(point);

        PointHistory pointHistory = new PointHistory(point, amount, TransactionType.CHARGE);
        pointHistoryRepository.save(pointHistory);

        return point;
    }

    public Point useWithLock(Long userId, Long amount) {
        Point point = pointRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new DomainException(PointErrorCode.POINT_BALANCE_NOT_FOUND));
        point.use(amount);
        pointRepository.save(point);

        PointHistory pointHistory = new PointHistory(point, amount, TransactionType.USE);
        pointHistoryRepository.save(pointHistory);

        return point;
    }
}
