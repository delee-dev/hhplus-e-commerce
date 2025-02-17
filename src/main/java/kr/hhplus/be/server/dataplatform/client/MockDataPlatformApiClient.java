package kr.hhplus.be.server.dataplatform.client;

import kr.hhplus.be.server.dataplatform.client.dto.SendPaymentInfoRequest;
import kr.hhplus.be.server.dataplatform.client.dto.SendPaymentInfoResponse;
import org.springframework.stereotype.Service;

@Service
public class MockDataPlatformApiClient implements DataPlatformApiClient {
    @Override
    public SendPaymentInfoResponse sendPaymentInfo(SendPaymentInfoRequest request) {
        return new SendPaymentInfoResponse(true);
    }
}
