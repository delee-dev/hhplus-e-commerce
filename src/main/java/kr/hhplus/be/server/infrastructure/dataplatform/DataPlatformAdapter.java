package kr.hhplus.be.server.infrastructure.dataplatform;

import kr.hhplus.be.server.application.payment.dto.PaymentResult;
import kr.hhplus.be.server.application.dataplatform.DataPlatformPort;
import kr.hhplus.be.server.dataplatform.client.dto.ApiRequest;
import kr.hhplus.be.server.dataplatform.client.DataPlatformApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataPlatformAdapter implements DataPlatformPort {
    private final DataPlatformApiClient dataPlatformApiClient;

    @Override
    public boolean call(PaymentResult paymentResult) {
        ApiRequest request = new ApiRequest(paymentResult.paymentId());
        return dataPlatformApiClient.call(request).success();
    }
}
