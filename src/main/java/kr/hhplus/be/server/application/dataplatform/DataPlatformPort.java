package kr.hhplus.be.server.application.dataplatform;

import kr.hhplus.be.server.application.dataplatform.dto.PaymentInfo;

public interface DataPlatformPort {
    boolean sendPaymentInfo(PaymentInfo paymentInfo);
}
