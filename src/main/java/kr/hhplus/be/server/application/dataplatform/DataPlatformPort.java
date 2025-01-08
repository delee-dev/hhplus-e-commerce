package kr.hhplus.be.server.application.dataplatform;

import kr.hhplus.be.server.application.payment.dto.PaymentResult;

public interface DataPlatformPort {
    boolean call(PaymentResult paymentResult);
}
