package com.joseyustiz.flightinterconnection.infrastructure.primary.web;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto.ApiErrorDto;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto.ApiErrorDto.ApiErrorDtoBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GetInterconnectedFlightHandler {
    private final GetInterconnectedFlightUseCase useCase;
    public static final String PARAMETER_IS_REQUIRED = "parameter is required";
    public static final String DEPARTURE_QUERY_PARAM = "departure";
    public static final String DEPARTURE_DATE_TIME_QUERY_PARAM = "departureDateTime";
    public static final String ARRIVAL_QUERY_PARAM = "arrival";
    public static final String ARRIVAL_DATE_TIME_QUERY_PARAM = "arrivalDateTime";

    public Mono<ServerResponse> getInterconnections(ServerRequest serverRequest) {
        final var apiError = verifyRequiredQueryParams(serverRequest);
        if (!apiError.getErrors().isEmpty())
            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(apiError);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(useCase.handle(new GetInterconnectedFlightUseCase.Query()), InterconnectedFlight.class);
    }

    private ApiErrorDto verifyRequiredQueryParams(ServerRequest serverRequest) {
        final var apiErrorBuilder = ApiErrorDto.builder();
        verifyRequiredQueryParam(serverRequest, apiErrorBuilder, DEPARTURE_QUERY_PARAM);
        verifyRequiredQueryParam(serverRequest, apiErrorBuilder, DEPARTURE_DATE_TIME_QUERY_PARAM);
        verifyRequiredQueryParam(serverRequest, apiErrorBuilder, ARRIVAL_QUERY_PARAM);
        verifyRequiredQueryParam(serverRequest, apiErrorBuilder, ARRIVAL_DATE_TIME_QUERY_PARAM);
        return apiErrorBuilder.build();
    }

    private void verifyRequiredQueryParam(ServerRequest serverRequest, ApiErrorDtoBuilder apiErrorBuilder, String queryParamName) {
        final var queryParam = serverRequest.queryParam(queryParamName);
        if (queryParam.isEmpty()) {
            apiErrorBuilder.error(new ApiErrorDto.Error(queryParamName, PARAMETER_IS_REQUIRED));
        }
    }

}
