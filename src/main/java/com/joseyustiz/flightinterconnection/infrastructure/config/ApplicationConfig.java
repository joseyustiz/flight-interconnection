package com.joseyustiz.flightinterconnection.infrastructure.config;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.port.secondary.CalculatePathPort;
import com.joseyustiz.flightinterconnection.core.port.secondary.RoutePort;
import com.joseyustiz.flightinterconnection.core.port.secondary.SchedulePort;
import com.joseyustiz.flightinterconnection.core.service.GetInterconnectedFlightService;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.InterconnectedFlightMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public GetInterconnectedFlightUseCase getInterconnectedFlightUseCase(RoutePort routePort, SchedulePort schedulePort, CalculatePathPort calculatePathPort){
        return new GetInterconnectedFlightService(routePort, schedulePort, calculatePathPort);
    }

    @Bean
    public InterconnectedFlightMapper getInterconnectedFlightMapper(){
        return InterconnectedFlightMapper.INSTANCE;
    }

}
