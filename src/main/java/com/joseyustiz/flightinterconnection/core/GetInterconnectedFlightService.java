package com.joseyustiz.flightinterconnection.core;

import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
public class GetInterconnectedFlightService implements GetInterconnectedFlightUseCase {
    private final GetInterconnectedFlightDelegate delegate;

    @Override
    public Flux<InterconnectedFlight> handle(Query query) {
        return Flux.fromIterable(delegate.getInterconnectedFlight(query));
    }

}
