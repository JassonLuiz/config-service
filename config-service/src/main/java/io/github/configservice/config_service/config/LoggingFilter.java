package io.github.configservice.config_service.config;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        try {
            MDC.put("traceId", UUID.randomUUID().toString());
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
