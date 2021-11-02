package com.joseyustiz.flightinterconnection.core;

import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.FlightDateTime;
import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import com.joseyustiz.flightinterconnection.core.domain.SelfValidating;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import reactor.core.publisher.Flux;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface GetInterconnectedFlightUseCase {
    Flux<InterconnectedFlight> handle(Query query);

    @EqualsAndHashCode(callSuper = false)
    @Value
    @Builder
    class Query extends SelfValidating<Query> {
        @NotNull(message = "must not be null")
        @Valid
        AirportIataCode departure;
        @NotNull(message = "must not be null")
        @Valid
        AirportIataCode arrival;
        @NotNull(message = "must not be null")
        @Valid
        FlightDateTime departureDateTime;
        @NotNull(message = "must not be null")
        @Valid
        FlightDateTime arrivalDateTime;

        public Query(AirportIataCode departure, AirportIataCode arrival, FlightDateTime departureDateTime, FlightDateTime arrivalDateTime) {
            this.departure = departure;
            this.arrival = arrival;
            this.departureDateTime = departureDateTime;
            this.arrivalDateTime = arrivalDateTime;
            this.validateSelf();
        }


    }
}
