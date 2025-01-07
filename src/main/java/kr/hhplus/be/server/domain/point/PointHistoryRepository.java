package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.model.PointHistory;

public interface PointHistoryRepository {
    PointHistory save(PointHistory pointHistory);
}
