package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.application.point.dto.PointResult;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointFacade {
    private final PointService pointService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public PointResult getPoint(Long userId) {
        User user = userService.getUser(userId);
        Point point = pointService.getPoint(userId);

        return new PointResult(
                user.getId(),
                user.getName(),
                point.getId(),
                point.getBalance()
        );
    }

    @Transactional
    public PointResult charge(Long userId, Long amount) {
        User user = userService.getUser(userId);
        Point point = pointService.chargeWithLock(userId, amount);

        return new PointResult(
                user.getId(),
                user.getName(),
                point.getId(),
                point.getBalance()
        );
    }
}
