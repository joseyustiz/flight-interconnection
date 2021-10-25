package com.joseyustiz.flightinterconnection.core;

import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import lombok.Value;
import reactor.core.publisher.Flux;

public interface GetInterconnectedFlightUseCase {
    Flux<InterconnectedFlight> handle(Query query);

    @Value
    public static class Query {
    }
}
