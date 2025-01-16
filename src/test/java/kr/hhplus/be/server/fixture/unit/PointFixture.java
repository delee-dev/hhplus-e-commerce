package kr.hhplus.be.server.fixture.unit;

import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.user.model.User;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;

public class PointFixture {
    public static Long VALID_CHARGE_AMOUNT = 50_000L;
    public static Long VALID_USE_AMOUNT = 10_000L;
    public static Long BELOW_MIN_CHARGE_AMOUNT = 500L;
    public static Long EXCEED_MAX_CHARGE_AMOUNT = 1_500_000L;

    private static User user() {
        return Instancio.of(User.class)
                .set(field("id"), 1L)
                .set(field("name"), "김철수")
                .create();
    }

    private static final Model<Point> basePoint = Instancio.of(Point.class)
            .set(field("id"), 1L)
            .set(field("balance"), 50_000L)
            .set(field("userId"), user().getId())
            .set(field("version"), 1L)
            .toModel();

    public static Point point() {
        return Instancio.of(basePoint)
                .create();
    }

    public static Point pointNearLimit() {
        return Instancio.of(basePoint)
                .set(field("balance"), 9_999_999L)
                .create();
    }
}
