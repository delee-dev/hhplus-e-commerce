package kr.hhplus.be.server.infrastructure.dataplatform;

import kr.hhplus.be.server.application.dataplatform.DataPlatformPort;
import kr.hhplus.be.server.application.dataplatform.dto.PaymentInfo;
import kr.hhplus.be.server.dataplatform.client.DataPlatformApiClient;
import kr.hhplus.be.server.dataplatform.client.dto.SendPaymentInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataPlatformAdapter implements DataPlatformPort {
    private final DataPlatformApiClient dataPlatformApiClient;

    @Override
    public boolean sendPaymentInfo(PaymentInfo paymentInfo) {
        SendPaymentInfoRequest request = new SendPaymentInfoRequest(paymentInfo.paymentId());
        return dataPlatformApiClient.sendPaymentInfo(request).success();
    }
}
