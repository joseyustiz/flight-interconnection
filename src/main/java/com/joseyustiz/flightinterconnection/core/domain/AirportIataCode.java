package com.joseyustiz.flightinterconnection.core.domain;

import lombok.Value;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Value
public class AirportIataCode {
    @Pattern(regexp = "[A-Z]{3}", message = "must have 3 alphabetic upper-letters characters")
    @NotNull(message = "must not be null")
    String value;
}
