package com.joseyustiz.flightinterconnection.infrastructure.primary.web;

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.FlightDateTime;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto.ApiErrorDto;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto.ApiErrorDto.ApiErrorDtoBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

import static com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightUseCase.Query;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetInterconnectedFlightHandler implements HandlerFunction<ServerResponse> {
    public static final String INVALID_DATETIME_FORMAT = "invalid format, it must be ISO-8601";
    private final GetInterconnectedFlightUseCase useCase;
    private final InterconnectedFlightMapper mapper;

    public static final String PARAMETER_IS_REQUIRED = "parameter is required";
    public static final String DEPARTURE_QUERY_PARAM = "departure";
    public static final String DEPARTURE_DATE_TIME_QUERY_PARAM = "departureDateTime";
    public static final String ARRIVAL_QUERY_PARAM = "arrival";
    public static final String ARRIVAL_DATE_TIME_QUERY_PARAM = "arrivalDateTime";
    private static final RuntimeException QUERY_PARAMETERS_VERIFICATION_FAILED = new RuntimeException("Query Parameters Verification failed");

    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        final var apiError = verifyRequiredQueryParams(serverRequest);
        if (!apiError.getErrors().isEmpty()) {
            log.info("HttpCode 400 with apiError {}", apiError);
            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(apiError);
        }
        final var departureDateTime = serverRequest.queryParam(DEPARTURE_DATE_TIME_QUERY_PARAM).orElseThrow(() -> QUERY_PARAMETERS_VERIFICATION_FAILED);
        final var arrivalDateTime = serverRequest.queryParam(ARRIVAL_DATE_TIME_QUERY_PARAM).orElseThrow(() -> QUERY_PARAMETERS_VERIFICATION_FAILED);
        try {
            final var query = Query.builder()
                    .departure(new AirportIataCode(serverRequest.queryParam(DEPARTURE_QUERY_PARAM).orElseThrow(() -> QUERY_PARAMETERS_VERIFICATION_FAILED)))
                    .arrival(new AirportIataCode(serverRequest.queryParam(ARRIVAL_QUERY_PARAM).orElseThrow(() -> QUERY_PARAMETERS_VERIFICATION_FAILED)))
                    .departureDateTime(new FlightDateTime(LocalDateTime.parse(departureDateTime, FlightDateTime.FORMATTER)))
                    .arrivalDateTime(new FlightDateTime(LocalDateTime.parse(arrivalDateTime, FlightDateTime.FORMATTER)))
                    .build();

            return Mono.from(useCase.handle(query)
                            .map(mapper::toDto)
                            .collectList()
                            .flatMap(interconnectedFlights -> {
                                log.info("HttpCode 200 for interconnectedFlights {} query {}",interconnectedFlights, query);
                                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(interconnectedFlights);
                            }))
                    .switchIfEmpty(Mono.defer(()->serverResponse200WithNoData(query)))
                    .onErrorResume(e -> {
                        log.info("HttpCode 500 for query = {}", query, e);
                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).build();
                    });
        } catch (ConstraintViolationException e) {
            final var errors = e.getConstraintViolations().stream()
                    .map(v -> new ApiErrorDto.Error(v.getPropertyPath().toString(), e.getMessage()))
                    .collect(Collectors.toList());
            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).body(ApiErrorDto.builder().errors(errors).build(), ApiErrorDto.class);
        }
    }

    private Mono<ServerResponse> serverResponse200WithNoData(Query query) {
        log.info("HttpCode 200 with empty body for query = {}", query);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).build();
    }

    private ApiErrorDto verifyRequiredQueryParams(ServerRequest serverRequest) {
        final var apiErrorBuilder = ApiErrorDto.builder();
        verifyRequiredQueryParam(serverRequest, apiErrorBuilder, DEPARTURE_QUERY_PARAM);
        verifyDateFormatQueryParam(serverRequest, apiErrorBuilder, DEPARTURE_DATE_TIME_QUERY_PARAM);
        verifyRequiredQueryParam(serverRequest, apiErrorBuilder, ARRIVAL_QUERY_PARAM);
        verifyDateFormatQueryParam(serverRequest, apiErrorBuilder, ARRIVAL_DATE_TIME_QUERY_PARAM);
        return apiErrorBuilder.build();
    }

    private void verifyRequiredQueryParam(ServerRequest serverRequest, ApiErrorDtoBuilder apiErrorBuilder, String queryParamName) {
        final var queryParam = serverRequest.queryParam(queryParamName);
        if (queryParam.isEmpty()) {
            apiErrorBuilder.error(new ApiErrorDto.Error(queryParamName, PARAMETER_IS_REQUIRED));
        }
    }


    private void verifyDateFormatQueryParam(ServerRequest serverRequest, ApiErrorDtoBuilder apiErrorBuilder, String queryParamName) {
        final var queryParam = serverRequest.queryParam(queryParamName);
        if (queryParam.isEmpty()) {
            apiErrorBuilder.error(new ApiErrorDto.Error(queryParamName, PARAMETER_IS_REQUIRED));
        }else{
            try {
                LocalDateTime.parse(queryParam.get(), FlightDateTime.FORMATTER);
            }catch (DateTimeParseException e){
                apiErrorBuilder.error(new ApiErrorDto.Error(queryParamName, INVALID_DATETIME_FORMAT));

            }
        }
    }

    private static class QueryDto{
        String departure;
        String arrival;
        String departureDateTime;
        String arrivalDateTime;

    }
}
