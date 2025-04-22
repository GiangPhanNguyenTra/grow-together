package com.example.ratelimiter.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RateLimiterService {
    // Dùng ConcurrentHashMap để lưu trữ buckets trong bộ nhớ một cách an toàn
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();
    
    @Autowired
    private PricingPlanService pricingPlanService;
    
    // Lấy hoặc tạo một bucket trong bộ nhớ cho một key
    public Bucket resolveBucket(String key) {
        return bucketCache.computeIfAbsent(key, this::createNewBucketForKey);

    }

    // Tạo bucket mới dựa trên key (để xác định plan)
    private Bucket createNewBucketForKey(String key) {
        PricingPlanService.Plan plan = pricingPlanService.resolvePlanForApiKey(key);
        log.info("Create new in-memory bucket for kay: {} with plan: {}", key, plan);

        return Bucket.builder()
                    .addLimit(plan.getLimit())
                    .build();
    }
}
