package kr.hhplus.be.server.event.payment.outbox;

import jakarta.persistence.*;
import kr.hhplus.be.server.event.payment.model.PaymentCompletedEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_event_outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEventOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private long paymentId;
    @Column(nullable = false)
    private String payload;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private boolean published;

    public static PaymentEventOutbox from(PaymentCompletedEvent event, String payload) {
        PaymentEventOutbox paymentEventOutbox = new PaymentEventOutbox();

        paymentEventOutbox.paymentId = event.getPaymentId();
        paymentEventOutbox.payload = payload;
        paymentEventOutbox.createdAt = event.getOccurredAt();

        return paymentEventOutbox;
    }

    public void checkPublished() {
        this.published = true;
    }
}
