package com.joseyustiz.flightinterconnection.infrastructure.primary.web;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import com.joseyustiz.flightinterconnection.infrastructure.config.ApplicationConfig;
import com.joseyustiz.flightinterconnection.infrastructure.config.WebRouterFunctionConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static com.joseyustiz.flightinterconnection.infrastructure.primary.web.GetInterconnectedFlightHandler.*;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest
@Import({WebRouterFunctionConfig.class, ApplicationConfig.class, GetInterconnectedFlightHandler.class})
public class GetInterconnectedFlightHandlerIT {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private GetInterconnectedFlightUseCase service;

    @Test
    void missingRequiredParameters_returnBadRequest() {
        //given:
        final var url = "/v1/flights/interconnections";
        //when:
        final var response = webClient.get()
                .uri(url)
                .exchange();
        //then:
        response.expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors[*].property")
                .value(hasItems(DEPARTURE_QUERY_PARAM, DEPARTURE_DATE_TIME_QUERY_PARAM,
                        ARRIVAL_QUERY_PARAM, ARRIVAL_DATE_TIME_QUERY_PARAM))
                .jsonPath("$.errors.size()").isEqualTo(4)
                .jsonPath("$.errors[*].message").value(hasItems(MUST_NOT_BE_NULL));


    }

    @Test
    void AnExceptionInTheService_returnInternalServerError() {
        //given:
        final var url = "/v1/flights/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00";
        when(service.handle(ArgumentMatchers.any())).thenThrow(new RuntimeException("Exception"));
        //when:
        final var response = webClient.get()
                .uri(url)
                .exchange();
        //then:
        response.expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Internal Server Error");

    }

    @Test
    void validValueFromTheService_returnProperContent() {
        //given:
        final var url = "/v1/flights/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00";
        when(service.handle(any())).thenReturn(Flux.just(InterconnectedFlight.builder().stops(0).build()));
        //when:
        final var response = webClient.get()
                .uri(url)
                .exchange();
        //then:
        response.expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.[0].stops").isEqualTo(0);

    }

    @Test
    void emptyInterconnectedFlight_returnEmptyBody() {
        //given:
        final var url = "/v1/flights/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00";
        when(service.handle(any())).thenReturn(Flux.empty());
        //when:
        final var response = webClient.get()
                .uri(url)
                .exchange();
        //then:
        response.expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().is2xxSuccessful()
                .expectBody().jsonPath("$.size()").isEqualTo(0);
    }

}
