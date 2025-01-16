package kr.hhplus.be.server.fixture.integration;

import kr.hhplus.be.server.domain.coupon.model.Coupon;
import kr.hhplus.be.server.domain.coupon.model.DiscountType;
import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.order.model.OrderItem;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.point.model.Point;
import kr.hhplus.be.server.domain.product.model.Category;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.SaleStatus;
import kr.hhplus.be.server.domain.product.model.Stock;
import kr.hhplus.be.server.domain.user.model.User;
import org.instancio.Instancio;
import org.instancio.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.instancio.Select.field;

public class Fixture {
    public static User user() {
        return Instancio.of(User.class)
                .set(field("id"), null)
                .set(field("name"), "김철수")
                .create();
    }

    public static Point point(User user) {
        return Instancio.of(Point.class)
                .set(field("id"), null)
                .set(field("balance"), 0L)
                .set(field("userId"), user.getId())
                .set(field("version"), 0L)
                .create();
    }

    public static Point point(User user, Long balance) {
        return Instancio.of(Point.class)
                .set(field("id"), null)
                .set(field("balance"), balance)
                .set(field("userId"), user.getId())
                .set(field("version"), 0L)
                .create();
    }

    public static Category category() {
        return Instancio.of(Category.class)
                .set(field("id"), null)
                .set(field("name"), "전자기기")
                .create();
    }

    public static Product product(Category category) {
        return Instancio.of(Product.class)
                .set(field("id"), null)
                .set(field("name"), "무선이어폰")
                .set(field("description"), "고음질 무선이어폰")
                .set(field("category"), category)
                .set(field("price"), 10_000L)
                .set(field("status"), SaleStatus.ON_SALE)
                .create();
    }

    public static Product product(Category category, Long price) {
        return Instancio.of(Product.class)
                .set(field("id"), null)
                .set(field("name"), "무선이어폰")
                .set(field("description"), "고음질 무선이어폰")
                .set(field("category"), category)
                .set(field("price"), price)
                .set(field("status"), SaleStatus.ON_SALE)
                .create();
    }

    private static final Model<Product> baseProduct = Instancio.of(Product.class)
            .set(field("id"), null)
            .set(field("status"), SaleStatus.ON_SALE)
            .toModel();

    public static List<Product> products(Category category) {
        return java.util.List.of(
                Instancio.of(baseProduct)
                        .set(field("category"), category)
                        .set(field("name"), "무선이어폰")
                        .set(field("description"), "고음질 무선이어폰")
                        .set(field("price"), 100000L)
                        .create(),
                Instancio.of(baseProduct)
                        .set(field("category"), category)
                        .set(field("name"), "태블릿PC")
                        .set(field("description"), "가성비 태블릿")
                        .set(field("price"), 200000L)
                        .create(),
                Instancio.of(baseProduct)
                        .set(field("category"), category)
                        .set(field("name"), "스마트폰")
                        .set(field("description"), "최신형 스마트폰")
                        .set(field("price"), 1200000L)
                        .set(field("status"), SaleStatus.SUSPENDED)
                        .create(),
                Instancio.of(baseProduct)
                        .set(field("category"), category)
                        .set(field("name"), "노트북")
                        .set(field("description"), "고성능 노트북")
                        .set(field("price"), 1500000L)
                        .create(),
                Instancio.of(baseProduct)
                        .set(field("category"), category)
                        .set(field("name"), "블루투스 스피커")
                        .set(field("description"), "고음질 휴대용 스피커")
                        .set(field("price"), 150000L)
                        .create(),
                Instancio.of(baseProduct)
                        .set(field("category"), category)
                        .set(field("name"), "스마트워치")
                        .set(field("description"), "건강관리 스마트워치")
                        .set(field("price"), 300000L)
                        .create(),
                Instancio.of(baseProduct)
                        .set(field("category"), category)
                        .set(field("name"), "프린터")
                        .set(field("description"), "컬러 레이저 프린터")
                        .set(field("price"), 350000L)
                        .create()
        );
    }

    public static Stock stock(Product product, int quantity) {
        return Instancio.of(Stock.class)
                .set(field("id"), null)
                .set(field("product"), product)
                .set(field("quantity"), quantity)
                .create();
    }

    private static List<OrderItem> orderItems(List<Product> products) {
        AtomicInteger quantity = new AtomicInteger(1);
        return products.stream().map(product -> {
            return Instancio.of(OrderItem.class)
                    .set(field("id"), null)
                    .set(field("order"), null)
                    .set(field("productId"), product.getId())
                    .set(field("productName"), product.getName())
                    .set(field("price"), product.getPrice())
                    .set(field("quantity"), quantity.getAndIncrement())
                    .create();
        }).toList();
    }

    public static List<Order> orders(User user, List<Product> products) {
        List<OrderItem> orderItems = orderItems(products);
        return orderItems.stream().map(orderItem -> {
            return new Order(
                    user.getId(),
                    List.of(orderItem),
                    "이다은",
                    "010-1234-1234",
                    "서울시 광진구 능동로"
            );
        }).toList();
    }

    public static List<Payment> payments(List<Order> orders) {
        AtomicInteger day = new AtomicInteger(1);
        return orders.stream().map(order -> {
            return Instancio.of(Payment.class)
                    .set(field("id"), null)
                    .set(field("orderId"), order.getId())
                    .set(field("totalAmount"), order.getTotalAmount())
                    .set(field("discountAmount"), 0L)
                    .set(field("finalAmount"), order.getTotalAmount())
                    .set(field("status"), PaymentStatus.COMPLETED)
                    .set(field("paidAt"), LocalDateTime.now().minusDays(day.getAndIncrement()))
                    .create();
        }).toList();
    }

    public static Coupon coupon(int quantity) {
        return Instancio.of(Coupon.class)
                .set(field("id"), null)
                .set(field("name"), "회원가입 쿠폰")
                .set(field("discountType"), DiscountType.FIXED_AMOUNT)
                .set(field("discountAmount"), 5_000L)
                .set(field("minOrderAmount"), 10_000L)
                .set(field("maxDiscountAmount"), null)
                .set(field("validFrom"), LocalDateTime.now().minusMonths(1))
                .set(field("validUntil"), LocalDateTime.now().plusMonths(1))
                .set(field("totalQuantity"), quantity)
                .create();
    }
}
