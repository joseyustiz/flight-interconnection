package com.joseyustiz.flightinterconnection.infrastructure.config;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightService;
import com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.port.secondary.CalculatePathPort;
import com.joseyustiz.flightinterconnection.core.port.secondary.RoutePort;
import com.joseyustiz.flightinterconnection.core.port.secondary.SchedulePort;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.InterconnectedFlightMapper;
import com.joseyustiz.flightinterconnection.infrastructure.secondary.RouteMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class ApplicationConfig {

    @Bean
    public GetInterconnectedFlightUseCase getInterconnectedFlightUseCase(RoutePort routePort, SchedulePort schedulePort, CalculatePathPort calculatePathPort){
        return new GetInterconnectedFlightService(routePort, schedulePort, calculatePathPort);
    }

    @Bean
    public InterconnectedFlightMapper getInterconnectedFlightMapper(){
        return InterconnectedFlightMapper.INSTANCE;
    }

    @Bean
    public RouteMapper getRouteMapper(){
        return RouteMapper.INSTANCE;
    }

}
