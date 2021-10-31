package com.joseyustiz.flightinterconnection.infrastructure.primary.web;

import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.FlightDateTime;
import com.joseyustiz.flightinterconnection.core.domain.FlightSchedule;
import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto.FlightScheduleDto;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto.InterconnectedFlightDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class InterconnectedFlightMapperTest {

    private static final String AIRPORT_DUB = "DUB";
    private static final String AIRPORT_WRO = "WRO";
    private static final String AIRPORT_STN = "STN";
    private static final LocalDateTime DEPARTURE_DATE_TIME = LocalDateTime.parse("2018-03-01T12:40");
    private static final LocalDateTime DEPARTURE_DATE_TIME2 = LocalDateTime.parse("2018-03-01T06:25");
    private static final LocalDateTime DEPARTURE_DATE_TIME3 = LocalDateTime.parse("2018-03-01T09:50");
    private static final LocalDateTime ARRIVAL_DATE_TIME = LocalDateTime.parse("2018-03-01T16:40");
    private static final LocalDateTime ARRIVAL_DATE_TIME2 = LocalDateTime.parse("2018-03-01T07:35");
    private static final LocalDateTime ARRIVAL_DATE_TIME3 = LocalDateTime.parse("2018-03-01T13:20");
    private static final FlightSchedule FLIGHT_SCHEDULE = FlightSchedule.builder()
            .departureAirport(new AirportIataCode(AIRPORT_DUB))
            .arrivalAirport(new AirportIataCode(AIRPORT_WRO))
            .departureDateTime(new FlightDateTime(DEPARTURE_DATE_TIME))
            .arrivalDateTime(new FlightDateTime(ARRIVAL_DATE_TIME))
            .build();

    private static final FlightSchedule FLIGHT_SCHEDULE2 = FlightSchedule.builder()
            .departureAirport(new AirportIataCode(AIRPORT_DUB))
            .arrivalAirport(new AirportIataCode(AIRPORT_STN))
            .departureDateTime(new FlightDateTime(DEPARTURE_DATE_TIME2))
            .arrivalDateTime(new FlightDateTime(ARRIVAL_DATE_TIME2))
            .build();

    private static final FlightSchedule FLIGHT_SCHEDULE3 = FlightSchedule.builder()
            .departureAirport(new AirportIataCode(AIRPORT_STN))
            .arrivalAirport(new AirportIataCode(AIRPORT_WRO))
            .departureDateTime(new FlightDateTime(DEPARTURE_DATE_TIME3))
            .arrivalDateTime(new FlightDateTime(ARRIVAL_DATE_TIME3))
            .build();
    private static final InterconnectedFlight INTERCONNECTED_FLIGHT = InterconnectedFlight.builder().stops(0).legs(List.of(FLIGHT_SCHEDULE)).build();
    private static final InterconnectedFlight INTERCONNECTED_FLIGHT2 = InterconnectedFlight.builder().stops(1).legs(List.of(FLIGHT_SCHEDULE2, FLIGHT_SCHEDULE3)).build();
    private static final InterconnectedFlightMapper MAPPER = InterconnectedFlightMapper.INSTANCE;

    @Test
    void nullInterconnectedFlight_mappedToNullDto() {
        //when:
        final var interconnectedFlightDto = MAPPER.toInterconnectedFlightDto(null);
        //then:
        assertThat(interconnectedFlightDto).isNull();

    }

    @Test
    void nullFlightScheduleDto_mappedToNullDto() {
        //when:
        final var flightScheduleDto = MAPPER.toFlightScheduleDto(null);
        //then:
        assertThat(flightScheduleDto).isNull();

    }

    @Test
    void validInterconnectedFlightWithCeroStops_mappedProperlyToDto() {
        InterconnectedFlightDto expectedDto = InterconnectedFlightDto.builder().stops(0).legs(List.of(FlightScheduleDto.builder()
                .departureAirport(AIRPORT_DUB).arrivalAirport(AIRPORT_WRO)
                .departureDateTime(DEPARTURE_DATE_TIME)
                .arrivalDateTime(ARRIVAL_DATE_TIME)
                .build())).build();
        //when:
        final var interconnectedFlightDto = MAPPER.toInterconnectedFlightDto(INTERCONNECTED_FLIGHT);
        //then:
        assertThat(interconnectedFlightDto).isEqualTo(expectedDto);

    }

    private static final FlightScheduleDto FLIGHT_SCHEDULE_DTO2 = FlightScheduleDto.builder()
            .departureAirport(AIRPORT_DUB).arrivalAirport(AIRPORT_STN)
            .departureDateTime(DEPARTURE_DATE_TIME2)
            .arrivalDateTime(ARRIVAL_DATE_TIME2)
            .build();
    private static final FlightScheduleDto FLIGHT_SCHEDULE_DTO3 = FlightScheduleDto.builder()
            .departureAirport(AIRPORT_STN).arrivalAirport(AIRPORT_WRO)
            .departureDateTime(DEPARTURE_DATE_TIME3)
            .arrivalDateTime(ARRIVAL_DATE_TIME3)
            .build();

    @Test
    void validInterconnectedFlightsWithOneStop_mappedProperlyToDto() {
        //given:
        InterconnectedFlightDto expectedDto = InterconnectedFlightDto.builder().stops(1).legs(List.of(FLIGHT_SCHEDULE_DTO2, FLIGHT_SCHEDULE_DTO3)).build();
        //when:
        final var interconnectedFlightDto = MAPPER.toInterconnectedFlightDto(INTERCONNECTED_FLIGHT2);
        //then:
        assertThat(interconnectedFlightDto).isEqualTo(expectedDto);

    }
}