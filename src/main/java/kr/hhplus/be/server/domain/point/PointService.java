package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.point.model.PointHistory;
import kr.hhplus.be.server.domain.point.model.TransactionType;
import kr.hhplus.be.server.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional(readOnly = true)
    public Point getPoint(Long userId) {
        return pointRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(PointErrorCode.POINT_BALANCE_NOT_FOUND));
    }

    @Transactional
    public Point chargeWithLock(Long userId, Long amount) {
        Point point = pointRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new BusinessException(PointErrorCode.POINT_BALANCE_NOT_FOUND));
        point.charge(amount);

        PointHistory pointHistory = new PointHistory(point, amount, TransactionType.CHARGE);
        pointHistoryRepository.save(pointHistory);

        return point;
    }

    @Transactional
    public Point useWithLock(Long userId, Long amount) {
        Point point = pointRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new BusinessException(PointErrorCode.POINT_BALANCE_NOT_FOUND));
        point.use(amount);

        PointHistory pointHistory = new PointHistory(point, amount, TransactionType.USE);
        pointHistoryRepository.save(pointHistory);

        return point;
    }
}
