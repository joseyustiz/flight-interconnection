package com.joseyustiz.flightinterconnection.core.domain;

import lombok.NonNull;
import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Value
public class AirportIataCode implements Serializable {
    public static final AirportIataCode NULL_AIRPORT = new AirportIataCode("NIL");
    @Pattern(regexp = "[A-Z]{3}", message = "must have 3 alphabetic upper-letters characters")
    @NotNull(message = "must not be null")
    String value;

    public String concat(@NonNull AirportIataCode airportIataCode) {
        return value +"-"+ airportIataCode;
    }
}
