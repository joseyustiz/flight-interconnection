package com.joseyustiz.flightinterconnection.infrastructure.primary.web;

import com.joseyustiz.flightinterconnection.core.domain.FlightDateTime;
import com.joseyustiz.flightinterconnection.core.domain.SelfValidating;
import com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto.ApiErrorDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static com.joseyustiz.flightinterconnection.core.domain.FlightDateTime.FORMATTER;
import static com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase.Query;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetInterconnectedFlightHandler implements HandlerFunction<ServerResponse> {

    public static final String INVALID_DATETIME_FORMAT = "invalid format, it must be ISO-8601";
    public static final String MUST_NOT_BE_NULL = "must not be null";
    public static final String MUST_HAVE_3_ALPHABETIC_UPPER_LETTERS_CHARACTERS = "must have 3 alphabetic upper-letters characters";

    public static final String DEPARTURE_QUERY_PARAM = "departure";
    public static final String DEPARTURE_DATE_TIME_QUERY_PARAM = "departureDateTime";
    public static final String ARRIVAL_QUERY_PARAM = "arrival";
    public static final String ARRIVAL_DATE_TIME_QUERY_PARAM = "arrivalDateTime";

    private final GetInterconnectedFlightUseCase useCase;
    private final InterconnectedFlightMapper flightMapper;

    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        try {
            QueryDto dto = buildQueryDto(serverRequest);

            return Mono.from(useCase.handle(QueryMapper.INSTANCE.toDomain(dto))
                            .map(flightMapper::toInterconnectedFlightDto)
                            .collectList()
                            .flatMap(interconnectedFlights -> {
                                log.info("HttpCode 200 for interconnectedFlights {} query {}", interconnectedFlights, dto);
                                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(interconnectedFlights);
                            })
//                    .onErrorResume(e -> {
//                        log.info("HttpCode 500 for query = {}", dto, e);
//                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).build();
//                    })

            );
        } catch (ConstraintViolationException e) {
            final var errors = e.getConstraintViolations().stream()
                    .map(v -> new ApiErrorDto.Error(v.getPropertyPath().toString(), v.getMessageTemplate()))
                    .collect(Collectors.toList());
            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).bodyValue(ApiErrorDto.builder().errors(errors).build());
        }
    }

    private QueryDto buildQueryDto(ServerRequest serverRequest) {
        return QueryDto.builder()
                .departureDateTime(serverRequest.queryParam(DEPARTURE_DATE_TIME_QUERY_PARAM).orElse(null))
                .arrivalDateTime(serverRequest.queryParam(ARRIVAL_DATE_TIME_QUERY_PARAM).orElse(null))
                .departure(serverRequest.queryParam(DEPARTURE_QUERY_PARAM).orElse(null))
                .arrival(serverRequest.queryParam(ARRIVAL_QUERY_PARAM).orElse(null))
                .build();
    }

    @EqualsAndHashCode(callSuper = false)
    @Builder
    @Value
    static class QueryDto extends SelfValidating<QueryDto> {
        @NotNull(message = MUST_NOT_BE_NULL)
        @Pattern(regexp = "[A-Z]{3}", message = MUST_HAVE_3_ALPHABETIC_UPPER_LETTERS_CHARACTERS)
        String departure;
        @NotNull(message = MUST_NOT_BE_NULL)
        @Pattern(regexp = "[A-Z]{3}", message = MUST_HAVE_3_ALPHABETIC_UPPER_LETTERS_CHARACTERS)
        String arrival;
        @Pattern(regexp = "(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2})\\:(\\d{2})", message = INVALID_DATETIME_FORMAT)
        @NotNull(message = MUST_NOT_BE_NULL)
        String departureDateTime;
        @Pattern(regexp = "(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2})\\:(\\d{2})", message = INVALID_DATETIME_FORMAT)
        @NotNull(message = MUST_NOT_BE_NULL)
        String arrivalDateTime;

        QueryDto(String departure, String arrival, String departureDateTime, String arrivalDateTime) {
            this.departure = departure;
            this.arrival = arrival;
            this.departureDateTime = departureDateTime;
            this.arrivalDateTime = arrivalDateTime;
            this.validateSelf();
        }
    }


    @Mapper
    interface QueryMapper {
        QueryMapper INSTANCE = Mappers.getMapper(QueryMapper.class);

        @Mapping(target = "departure.value", source = "departure")
        @Mapping(target = "arrival.value", source = "arrival")
        @Mapping(target = "departureDateTime", source = "departureDateTime", qualifiedByName = "mapToFlightDateTime")
        @Mapping(target = "arrivalDateTime", source = "arrivalDateTime", qualifiedByName = "mapToFlightDateTime")
        Query toDomain(QueryDto dto);

        @Named("mapToFlightDateTime")
        default FlightDateTime mapToFlightDateTime(String value) {
            if (value == null) {
                return null;
            }
            return new FlightDateTime(LocalDateTime.parse(value, FORMATTER));
        }

    }
}
