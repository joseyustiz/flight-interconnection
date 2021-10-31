package com.joseyustiz.flightinterconnection.infrastructure.primary.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GetInterconnectedFlightHandlerTest {
    @Test
    void nullParameterToMapToFlightDateTime_returnNull(){
        final var flightDateTime = GetInterconnectedFlightHandler.QueryMapper.INSTANCE.mapToFlightDateTime(null);

        assertThat(flightDateTime).isNull();
    }
}