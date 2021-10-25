package com.joseyustiz.flightinterconnection.core.service;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import reactor.core.publisher.Flux;

public class GetInterconnectedFlightService implements GetInterconnectedFlightUseCase {
    @Override
    public Flux<InterconnectedFlight> handle(Query query) {
        return Flux.empty();
    }
}
