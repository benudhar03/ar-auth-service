package com.baseoauth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * CORS Filter to handle cross-origin requests
 * This filter allows all origins and handles preflight OPTIONS requests
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String origin = request.getHeader("Origin");

        // Log CORS request for debugging
        if (log.isDebugEnabled()) {
            log.debug("CORS Request - Method: {}, Origin: {}, URI: {}",
                    request.getMethod(), origin, request.getRequestURI());
        }

        // Set CORS headers
        response.setHeader("Access-Control-Allow-Origin", getAllowedOrigin(origin));
        response.setHeader("Access-Control-Allow-Methods", getAllowMethods());
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", getAllowHeaders());
        response.setHeader("Access-Control-Expose-Headers", getExposeHeaders());
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // Handle preflight OPTIONS request
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            if (log.isDebugEnabled()) {
                log.debug("Handled OPTIONS preflight request for origin: {}", origin);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Get allowed origin - can be customized based on environment
     * @param origin The request origin
     * @return Allowed origin
     */
    private String getAllowedOrigin(String origin) {
        // For production, you might want to check against a list of allowed origins
        // For development, allow all origins
        // You can also read from application properties
        return "*";

        // Example for specific origins:
        // List<String> allowedOrigins = Arrays.asList("https://example.com", "https://app.example.com");
        // return allowedOrigins.contains(origin) ? origin : "*";
    }

    /**
     * Get allowed HTTP methods
     * @return Comma-separated list of allowed methods
     */
    private String getAllowMethods() {
        return "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD";
    }

    /**
     * Get allowed headers
     * @return Comma-separated list of allowed headers
     */
    private String getAllowHeaders() {
        return "authorization, content-type, xsrf-token, X-Requested-With, " +
                "Accept, Accept-Language, Content-Language, Cache-Control, " +
                "X-CSRF-TOKEN, X-XSRF-TOKEN, X-Auth-Token";
    }

    /**
     * Get exposed headers (headers that can be accessed by client JavaScript)
     * @return Comma-separated list of exposed headers
     */
    private String getExposeHeaders() {
        return "xsrf-token, Authorization, X-XSRF-TOKEN, X-CSRF-TOKEN, " +
                "X-Auth-Token, X-Total-Count, X-Pagination-Page, X-Pagination-Limit";
    }
}