package com.joseyustiz.flightinterconnection.infrastructure.config;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ApplicationConfigTest {

    @Test
    void validGetInterconnectedFlightService(){
        final var useCase = new ApplicationConfig().getInterconnectedFlightUseCase();

        assertThat(useCase).isNotNull();
        assertThat(useCase).isInstanceOf(GetInterconnectedFlightUseCase.class);
    }

}