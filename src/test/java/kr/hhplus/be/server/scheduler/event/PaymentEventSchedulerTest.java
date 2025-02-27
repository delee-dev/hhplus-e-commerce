package kr.hhplus.be.server.scheduler.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.event.payment.model.PaymentCompletedEvent;
import kr.hhplus.be.server.event.payment.outbox.PaymentEventOutbox;
import kr.hhplus.be.server.infrastructure.payment.event.persistence.PaymentEventOutboxJpaRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@SpringBootTest
@Sql("/clear.sql")
@EmbeddedKafka(topics = "payment-test")
public class PaymentEventSchedulerTest {
    @Autowired
    private PaymentEventOutboxJpaRepository paymentEventOutboxRepository;
    @Autowired
    private PaymentEventScheduler paymentEventScheduler;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        Payment payment = Instancio.of(Payment.class)
                .set(field("id"), 1L)
                .set(field("orderId"), 1L)
                .set(field("totalAmount"), 100_000L)
                .set(field("discountAmount"), 0L)
                .set(field("finalAmount"), 100_000L)
                .set(field("status"), PaymentStatus.PENDING)
                .create();
        PaymentCompletedEvent paymentCompletedEvent = PaymentCompletedEvent.from(payment);
        PaymentEventOutbox paymentEventOutbox = Instancio.of(PaymentEventOutbox.class)
                .set(field("id"), null)
                .set(field("paymentId"), paymentCompletedEvent.getPaymentId())
                .set(field("payload"), objectMapper.writeValueAsString(paymentCompletedEvent))
                .set(field("createdAt"), LocalDateTime.now().minusMinutes(10))
                .set(field("published"), false)
                .create();
        paymentEventOutboxRepository.saveAndFlush(paymentEventOutbox);
    }

    @Test
    public void 실패한_이벤트는_스케줄러를_통해_재발행된다() throws InterruptedException {
        // given
        Long paymentId = 1L;

        // when
        paymentEventScheduler.retryPublish();

        Thread.sleep(500);

        // then
        assertThat(paymentEventOutboxRepository.findByPaymentId(paymentId).isPublished()).isEqualTo(true);
    }
}
