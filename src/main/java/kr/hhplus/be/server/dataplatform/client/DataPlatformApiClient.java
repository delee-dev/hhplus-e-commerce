package kr.hhplus.be.server.dataplatform.client;

import kr.hhplus.be.server.dataplatform.client.dto.SendPaymentInfoRequest;
import kr.hhplus.be.server.dataplatform.client.dto.SendPaymentInfoResponse;

public interface DataPlatformApiClient {
    SendPaymentInfoResponse sendPaymentInfo(SendPaymentInfoRequest request);
}
