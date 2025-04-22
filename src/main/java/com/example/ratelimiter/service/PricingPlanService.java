package com.example.ratelimiter.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

@Service
public class PricingPlanService {
    public enum Plan {
        FREE(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)))),   
        BASIC(Bandwidth.classic(40, Refill.greedy(40, Duration.ofMinutes(1)))),
        PRO(Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1))));
        
        private final Bandwidth limit;

        Plan(Bandwidth limit) {
            this.limit = limit;
        }

        public Bandwidth getLimit() {return limit;}
    }


    // Giả sử lấy Plan dựa trên API KEY
    public Plan resolvePlanForApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty())
            return Plan.FREE;
        if (apiKey.startsWith("PRO-"))
            return Plan.PRO;
        if (apiKey.startsWith("BSC-"))
            return Plan.BASIC;
        return Plan.FREE;
    }

}
