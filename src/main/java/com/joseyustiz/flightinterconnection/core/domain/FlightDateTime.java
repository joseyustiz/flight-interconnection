package com.joseyustiz.flightinterconnection.core.domain;

import lombok.Value;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Value
public class FlightDateTime {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    @NotNull
    LocalDateTime value;

    public FlightDateTime(LocalDateTime value) {
        this.value = value;
    }
}
