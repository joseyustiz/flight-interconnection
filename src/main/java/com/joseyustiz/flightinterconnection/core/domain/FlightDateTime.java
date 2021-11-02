package com.joseyustiz.flightinterconnection.core.domain;

import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Value
public class FlightDateTime {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    @NotNull
    LocalDateTime value;

    public FlightDateTime(LocalDateTime value) {
        this.value = value;
    }

    public List<ScheduleYearMonth> getListOfDatesUntil(@NonNull FlightDateTime flightDateTime) {
        List<ScheduleYearMonth> dates = new ArrayList<>();

        for (YearMonth d = YearMonth.from(value); !d.isAfter(YearMonth.from(flightDateTime.getValue())); d = d.plusMonths(1)) {
            dates.add(new ScheduleYearMonth(d));
        }
        return dates;
    }

    public boolean isNotBefore(FlightDateTime flightDateTime) {
        return !value.isBefore(flightDateTime.getValue());
    }

    public boolean isNotAfter(FlightDateTime flightDateTime) {
        return !value.isAfter(flightDateTime.getValue());
    }
}
