package com.joseyustiz.flightinterconnection.infrastructure.config;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.service.GetInterconnectedFlightService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public GetInterconnectedFlightUseCase getInterconnectedFlightUseCase(){
        return new GetInterconnectedFlightService();
    }
}
