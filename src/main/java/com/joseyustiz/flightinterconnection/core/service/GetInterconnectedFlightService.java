package com.joseyustiz.flightinterconnection.core.service;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
@Slf4j
public class GetInterconnectedFlightService implements GetInterconnectedFlightUseCase {
    @Override
    public Flux<InterconnectedFlight> handle(Query query) {
        log.info("Query = {}", query);
        return Flux.empty();
    }
}
