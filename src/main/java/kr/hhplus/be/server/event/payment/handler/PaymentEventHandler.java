package kr.hhplus.be.server.event.payment.handler;

import kr.hhplus.be.server.application.dataplatform.DataPlatformPort;
import kr.hhplus.be.server.application.dataplatform.dto.PaymentInfo;
import kr.hhplus.be.server.event.payment.model.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventHandler {
    private final DataPlatformPort dataPlatformPort;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendToDataPlatform(PaymentCompletedEvent event) {
        PaymentInfo paymentInfo = new PaymentInfo(event.getPaymentId(), event.getPaidAt());
        dataPlatformPort.sendPaymentInfo(paymentInfo);
    }
}
