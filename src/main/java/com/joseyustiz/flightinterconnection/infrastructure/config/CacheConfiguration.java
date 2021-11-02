package com.joseyustiz.flightinterconnection.infrastructure.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {
    @Bean
    Config config() {
        Config config = new Config();

        MapConfig mapConfig = new MapConfig();
        mapConfig.setTimeToLiveSeconds(60*60);
        config.getMapConfigs().put("schedules", mapConfig);
        config.getMapConfigs().put("routes", mapConfig);
        config.getMapConfigs().put("interconnectedFlights", mapConfig);

        return config;
    }
}
