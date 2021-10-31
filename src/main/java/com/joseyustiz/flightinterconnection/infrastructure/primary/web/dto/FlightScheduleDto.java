package com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlightScheduleDto {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    String departureAirport;
    String arrivalAirport;
    @JsonIgnore
    LocalDateTime departureDateTime;
    @JsonIgnore
    LocalDateTime arrivalDateTime;

    @JsonProperty("departureDateTime")
    public String getDepartureDateTimeForJackson() {

        return this.departureDateTime == null ? null : this.departureDateTime.format(FORMATTER);
    }

    @JsonProperty("arrivalDateTime")
    public String getArrivalDateTimeForJackson() {
        return this.arrivalDateTime == null ? null : this.arrivalDateTime.format(FORMATTER);
    }
}
