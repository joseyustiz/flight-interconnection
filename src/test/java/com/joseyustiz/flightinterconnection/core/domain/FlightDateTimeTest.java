package com.joseyustiz.flightinterconnection.core.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FlightDateTimeTest {
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2021, 10, 30, 22, 0);

    @Test
    void nullArrivalDate_returnIllegalArgumentException() {
        FlightDateTime departure = new FlightDateTime(LOCAL_DATE_TIME);
        Assertions.assertThrows(IllegalArgumentException.class, ()->departure.getListOfDatesUntil(null));

    }

    @Test
    void arrivalDateAfterDeparture_returnListOfDatesBetweenThem() {
        FlightDateTime departure = new FlightDateTime(LOCAL_DATE_TIME);
        final var months = 5;
        FlightDateTime arrival = new FlightDateTime(LOCAL_DATE_TIME.plusMonths(months));
        final var dates = departure.getListOfDatesUntil(arrival);

        List<ScheduleYearMonth> expectedDates = new ArrayList<>();
        for(int i = 0; i<=months; i++) {
                expectedDates.add(new ScheduleYearMonth(YearMonth.from(LOCAL_DATE_TIME.toLocalDate().plusMonths(i))));
        }
        assertThat(dates).isEqualTo(expectedDates);
    }
}