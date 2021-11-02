package com.joseyustiz.flightinterconnection.infrastructure.config;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightDelegate;
import com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.port.secondary.CalculatePathPort;
import com.joseyustiz.flightinterconnection.core.port.secondary.RoutePort;
import com.joseyustiz.flightinterconnection.core.port.secondary.SchedulePort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ApplicationConfigTest {

    @Test
    void validGetInterconnectedFlightService(){
        RoutePort routePort = Mockito.mock(RoutePort.class);
        SchedulePort schedulePort = Mockito.mock(SchedulePort.class);
        CalculatePathPort calculatePathPort = Mockito.mock(CalculatePathPort.class);
        GetInterconnectedFlightDelegate delegate = new GetInterconnectedFlightDelegate(routePort, schedulePort, calculatePathPort);
        final var useCase = new ApplicationConfig().getInterconnectedFlightUseCase(delegate);

        assertThat(useCase).isNotNull();
        assertThat(useCase).isInstanceOf(GetInterconnectedFlightUseCase.class);
    }

}