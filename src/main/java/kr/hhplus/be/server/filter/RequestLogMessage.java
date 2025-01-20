package kr.hhplus.be.server.filter;

import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record RequestLogMessage(
    String type,
    String traceId,
    LocalDateTime timeStamp,
    String clientIp,
    String method,
    String uri,
    String queryString,
    Map<String, String> headers,
    String body
) {
    public static RequestLogMessage of(ContentCachingRequestWrapper request, String traceId) {
        return new RequestLogMessage(
                "REQUEST",
                traceId,
                LocalDateTime.now(),
                request.getRemoteAddr(),
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                getHeaders(request),
                getBody(request)
        );
    }

    private static Map<String, String> getHeaders(ContentCachingRequestWrapper request) {
        List<String> SENSITIVE_HEADERS = Arrays.asList("authorization", "password", "token", "secret");

        return Collections.list(request.getHeaderNames()).stream()
                .filter(name -> !SENSITIVE_HEADERS.contains(name))
                .collect(Collectors.toMap(Function.identity(), request::getHeader));
    }

    private static String getBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return "";
    }
}
