package com.joseyustiz.flightinterconnection.infrastructure.primary.web;

import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.FlightDateTime;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase.Query;
import static com.joseyustiz.flightinterconnection.infrastructure.primary.web.GetInterconnectedFlightHandler.QueryDto;
import static com.joseyustiz.flightinterconnection.infrastructure.primary.web.GetInterconnectedFlightHandler.QueryMapper;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GetInterconnectedFlightHandler$QueryMapperTest {
    private static final QueryMapper MAPPER = QueryMapper.INSTANCE;
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2021, 10, 30, 22, 0);

    @Test
    void nullDto_toDomainReturnNull() {
        final var query = MAPPER.toDomain(null);
        assertThat(query).isNull();
    }

    @Test
    void validDto_toDomainReturnValidDomain() {
        final var dto = QueryDto.builder()
                .departure("AAA").arrival("BBB").departureDateTime("2021-10-30T22:00")
                .arrivalDateTime("2021-10-30T23:00").build();
        final var query = MAPPER.toDomain(dto);
        final var expectedQuery = Query.builder()
                .arrivalDateTime(new FlightDateTime(LOCAL_DATE_TIME.plusHours(1)))
                .departureDateTime(new FlightDateTime(LOCAL_DATE_TIME))
                .departure(new AirportIataCode("AAA"))
                .arrival(new AirportIataCode("BBB")).build();

        assertThat(query).isEqualTo(expectedQuery);
    }
}