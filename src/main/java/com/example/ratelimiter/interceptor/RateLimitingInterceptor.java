package com.example.ratelimiter.interceptor;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.ratelimiter.service.RateLimiterService;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RateLimitingInterceptor implements HandlerInterceptor{

    @Autowired
    private RateLimiterService rateLimiterService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String apiKey = request.getHeader("X-api-key");

        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Missing API Key header");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing API Key Header");
            return false;
        }

        // Get the bucket from In-memory cache management service
        Bucket bucket = rateLimiterService.resolveBucket(apiKey);
        
        // Try consume 1 token from the bucket
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // tokens are still available
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            log.debug("Request allowed for API Key: {}. Remaining tokens: {}", apiKey, probe.getRemainingTokens());

            return true; // allow the request to proceed
        } else {
            // no token left
            long waitForRefillNonos = probe.getNanosToWaitForRefill();
            long retryAfterSeconds = TimeUnit.NANOSECONDS.toSeconds(waitForRefillNonos);

            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds > 0 ? retryAfterSeconds : 1));
            response.setHeader("X-Rate-Limit-Remaining", "0");

            log.warn("Rate limit exceeded for API Key: {}. Need to wait {} seconds.", apiKey, retryAfterSeconds);

            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "You have exhausted your API request quota. Try again Later.");
                
            return false; // block/deny the request
        }
    }

}
