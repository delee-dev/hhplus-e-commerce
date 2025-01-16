package kr.hhplus.be.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString();

        try {
            MDC.put("traceId", traceId);

            // 요청 로깅
            RequestLogMessage requestLogMessage = RequestLogMessage.of(requestWrapper, traceId);
            log.info(objectMapper.writeValueAsString(requestLogMessage));

            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // 응답 로깅
            long duration = System.currentTimeMillis() - startTime;
            ResponseLogMessage responseLogMessage = ResponseLogMessage.of(responseWrapper, traceId, duration);
            log.info(objectMapper.writeValueAsString(responseLogMessage));

            MDC.clear();

            responseWrapper.copyBodyToResponse();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/actuator");
    }
}
