package com.joseyustiz.flightinterconnection.core.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AirportIataCodeTest {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @ParameterizedTest
    @ValueSource(strings = {"", "3", "", "-3", "15", "  ", "A", "AA", "aaa", "AAa"})
    void invalidValue_areDetectedByValidator(String values) {
        final var airportIataCode = new AirportIataCode(values);

        final var violationSet = validator.validate(airportIataCode);

        assertThat(violationSet.size()).isEqualTo(1);
        assertThat(violationSet.stream().findFirst().get().getMessage()).isEqualTo("must have 3 alphabetic upper-letters characters");

    }


}