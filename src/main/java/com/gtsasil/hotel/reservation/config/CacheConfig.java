package com.gtsasil.hotel.reservation.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // This tells Spring Boot: "Turn on the caching engine!"
}
