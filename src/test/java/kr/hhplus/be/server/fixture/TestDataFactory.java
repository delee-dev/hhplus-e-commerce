package kr.hhplus.be.server.fixture;

import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.user.model.User;
import org.instancio.Instancio;

import static org.instancio.Select.field;

public class TestDataFactory {
    public static class UserConstants {
        public static final long NON_EXISTENT_USER_ID = 999;
        public static final long EXISTENT_USER_ID = 1;
    }

    public static class PointConstants {
        public static final long VALID_CHARGE_AMOUNT = 10_000;
        public static final long BELOW_MIN_CHARGE_AMOUNT = 900;
        public static final long EXCEED_MAX_CHARGE_AMOUNT = 1_500_000;
        public static final long VALID_USE_AMOUNT = 10_000L;
        public static final long EXCEED_BALANCE_AMOUNT = 60_000L;
    }

    public static User createUser() {
        return Instancio.of(User.class)
                .set(field("id"), 1L)
                .set(field("name"), "김철수")
                .create();
    }

    public static Point createPoint() {
        User user = createUser();
        return Instancio.of(Point.class)
                .set(field("id"), 1L)
                .set(field("balance"), 50_000L)
                .set(field("userId"), user.getId())
                .set(field("version"), 1L)
                .create();
    }

    public static Point createNearLimitPoint() {
        User user = createUser();
        return Instancio.of(Point.class)
                .set(field("id"), 1L)
                .set(field("balance"), 9_999_999L)
                .set(field("userId"), user.getId())
                .set(field("version"), 1L)
                .create();
    }

}
