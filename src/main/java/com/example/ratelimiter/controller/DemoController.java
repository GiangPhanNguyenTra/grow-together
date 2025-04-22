package com.example.ratelimiter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/greeting")
    public String getGreeting(@RequestHeader(value = "X-api-key", required = false) String apiKey) {
        return "Hello ! Your request was processed";
    }
}
