package com.joseyustiz.flightinterconnection.core.service;

import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolationException;

import static com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase.Query;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetInterconnectedFlightServiceTest {
    @Test
    void invalidQueryValues_throwsConstraintViolationException() {
        final var exception = assertThrows(ConstraintViolationException.class, () -> Query.builder().departure(new AirportIataCode("")).arrival(new AirportIataCode("")).departureDateTime(null).arrivalDateTime(null).build());
        assertThat(exception.getConstraintViolations().size()).isEqualTo(4);

    }
}