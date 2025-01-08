package kr.hhplus.be.server.dataplatform.client;

import kr.hhplus.be.server.dataplatform.client.dto.ApiRequest;
import kr.hhplus.be.server.dataplatform.client.dto.ApiResponse;

public interface DataPlatformApiClient {
    ApiResponse call(ApiRequest request);
}
