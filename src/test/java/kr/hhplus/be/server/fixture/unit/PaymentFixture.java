package kr.hhplus.be.server.fixture.unit;

import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import org.instancio.Instancio;
import org.instancio.Model;

import static org.instancio.Select.field;

public class PaymentFixture {
    private static final Model<Payment> basePayment = Instancio.of(Payment.class)
            .set(field("id"), 1L)
            .set(field("orderId"), 1L)
            .set(field("totalAmount"), 100_000L)
            .set(field("discountAmount"), 0L)
            .set(field("finalAmount"), 100_000L)
            .set(field("status"), PaymentStatus.PENDING)
            .toModel();

    public static Payment payment() {
        return Instancio.of(basePayment)
                .create();
    }

    public static Payment completedPayment() {
        return Instancio.of(basePayment)
                .set(field("status"), PaymentStatus.COMPLETED)
                .create();
    }

    public static Payment canceledPayment() {
        return Instancio.of(basePayment)
                .set(field("status"), PaymentStatus.CANCELED)
                .create();
    }
}
