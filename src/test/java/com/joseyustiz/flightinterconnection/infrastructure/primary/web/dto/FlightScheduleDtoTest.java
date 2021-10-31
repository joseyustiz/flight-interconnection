package com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FlightScheduleDtoTest {

    public static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2021, 10, 30, 22, 0);
    private static final FlightScheduleDto FLIGHT_SCHEDULE_DTO = FlightScheduleDto.builder().departureDateTime(LOCAL_DATE_TIME).arrivalDateTime(LOCAL_DATE_TIME.plusHours(1)).departureAirport("AAA").arrivalAirport("BBB").build();
    private static final FlightScheduleDto FLIGHT_SCHEDULE_DTO_WITH_NULL_DATE_TIME = FlightScheduleDto.builder().departureDateTime(null).arrivalDateTime(null).departureAirport("AAA").arrivalAirport("BBB").build();

    @Test
    void nullDepartureDateTimeForJackson_getDepartureDateTimeForJacksonReturnNull() {
        assertThat(FLIGHT_SCHEDULE_DTO_WITH_NULL_DATE_TIME.getDepartureDateTimeForJackson()).isNull();
    }

    @Test
    void nullArrivalDateTimeForJackson_getDepartureDateTimeForJacksonReturnNull() {
        assertThat(FLIGHT_SCHEDULE_DTO_WITH_NULL_DATE_TIME.getArrivalDateTimeForJackson()).isNull();
    }

    @Test
    void validDepartureDateTimeForJacksonValue_returnGetDepartureDateTimeForJacksonInProperFormat() {
        assertThat(FLIGHT_SCHEDULE_DTO.getDepartureDateTimeForJackson()).isEqualTo("2021-10-30T22:00");
    }
    @Test
    void validArrivalDateTimeForJacksonValue_returnGetDepartureDateTimeForJacksonInProperFormat() {
        assertThat(FLIGHT_SCHEDULE_DTO.getArrivalDateTimeForJackson()).isEqualTo("2021-10-30T23:00");
    }
}