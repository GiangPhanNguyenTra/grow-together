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
    // Use ConcurrentHashMap to safely store buckets in memory
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();
    
    @Autowired
    private PricingPlanService pricingPlanService;
    
    // Get or Create a bucket in memory for a key
    public Bucket resolveBucket(String key) {
        return bucketCache.computeIfAbsent(key, this::createNewBucketForKey);

    }

    // Create a new bucket based on the key (to determine the Plan)
    private Bucket createNewBucketForKey(String key) {
        PricingPlanService.Plan plan = pricingPlanService.resolvePlanForApiKey(key);
        log.info("Create new in-memory bucket for kay: {} with plan: {}", key, plan);

        return Bucket.builder()
                    .addLimit(plan.getLimit())
                    .build();
    }
}
