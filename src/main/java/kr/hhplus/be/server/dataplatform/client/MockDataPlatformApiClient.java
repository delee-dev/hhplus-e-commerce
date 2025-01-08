package kr.hhplus.be.server.dataplatform.client;

import kr.hhplus.be.server.dataplatform.client.dto.ApiRequest;
import kr.hhplus.be.server.dataplatform.client.dto.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public class MockDataPlatformApiClient implements DataPlatformApiClient {
    @Override
    public ApiResponse call(ApiRequest request) {
        return new ApiResponse(true);
    }
}
