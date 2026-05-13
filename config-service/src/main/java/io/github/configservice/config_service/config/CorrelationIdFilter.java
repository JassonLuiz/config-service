package io.github.configservice.config_service.config;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID = "correlationId";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;


        String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);

        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
            log.debug("Generated a new correlationId: {}", correlationId);
        } else {
            log.debug("Using existing correlationId: {}", correlationId);
        }

        MDC.put(CORRELATION_ID, correlationId);

        httpResponse.addHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID);
        }
    }
}
