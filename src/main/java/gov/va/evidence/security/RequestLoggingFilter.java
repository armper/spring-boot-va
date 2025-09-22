package gov.va.evidence.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Map;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            MDC.put("traceId", UUID.randomUUID().toString());
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            try {
                Charset charset = Optional.ofNullable(wrappedRequest.getCharacterEncoding())
                        .map(Charset::forName)
                        .orElse(StandardCharsets.UTF_8);
                String body = new String(wrappedRequest.getContentAsByteArray(), charset);
                String veteranIdMasked = extractAndMaskVeteranId(body);
                log.info("{} {} - veteranId:{} status:{}", request.getMethod(), request.getRequestURI(), veteranIdMasked, wrappedResponse.getStatus());
            } catch (Exception e) {
                log.info("{} {} - status:{}", request.getMethod(), request.getRequestURI(), wrappedResponse.getStatus());
            }
            wrappedResponse.copyBodyToResponse();
        }
    }

    private String extractAndMaskVeteranId(String body) {
        if (body == null || body.isBlank()) return "N/A";
        try {
            Map<String, Object> map = mapper.readValue(body, new TypeReference<Map<String, Object>>(){});
            Object raw = map.get("veteranId");
            if (raw == null) return "N/A";
            return mask(String.valueOf(raw));
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String mask(String input) {
        if (input == null) return "N/A";
        String s = input.trim();
        if (s.length() <= 2) return s;
        return "*".repeat(s.length() - 2) + s.substring(s.length() - 2);
    }
}
