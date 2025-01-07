package kr.hhplus.be.server.application.point.dto;

import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.user.model.User;

public record PointResult(
        long userId,
        String userName,
        long pointId,
        long balance
) {
    public static PointResult fromEntity(User user, Point point) {
        return new PointResult(user.getId(), user.getName(), point.getId(), point.getBalance());
    }
}
