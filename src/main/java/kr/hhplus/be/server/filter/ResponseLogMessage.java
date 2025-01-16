package kr.hhplus.be.server.filter;

import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;

public record ResponseLogMessage(
    String type,
    String traceId,
    long duration,
    int statusCode,
    long responseSize,
    String body
) {
    public static ResponseLogMessage of(ContentCachingResponseWrapper response, String traceId, long duration) {
        return new ResponseLogMessage(
                "RESPONSE",
                traceId,
                duration,
                response.getStatus(),
                response.getContentSize(),
                getBody(response)
        );
    }

    private static String getBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return "";
    }
}
