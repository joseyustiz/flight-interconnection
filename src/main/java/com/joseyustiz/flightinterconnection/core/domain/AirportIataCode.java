package com.joseyustiz.flightinterconnection.core.domain;

import lombok.Value;

import javax.validation.constraints.Pattern;
@Value
public class AirportIataCode {
    @Pattern(regexp = "[A-Z]{3}", message = "must have 3 alphabetic upper-letters characters")
    String value;
}
