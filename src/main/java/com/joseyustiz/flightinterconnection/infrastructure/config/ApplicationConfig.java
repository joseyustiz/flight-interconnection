package com.joseyustiz.flightinterconnection.infrastructure.config;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.service.GetInterconnectedFlightService;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.InterconnectedFlightMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public GetInterconnectedFlightUseCase getInterconnectedFlightUseCase(){
        return new GetInterconnectedFlightService();
    }

    @Bean
    public InterconnectedFlightMapper getInterconnectedFlightMapper(){
        return InterconnectedFlightMapper.INSTANCE;
    }

}
