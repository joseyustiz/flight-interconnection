package com.joseyustiz.flightinterconnection.infrastructure.secondary;

import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.FlightSchedule;
import com.joseyustiz.flightinterconnection.core.domain.IntegrationException;
import com.joseyustiz.flightinterconnection.core.domain.ScheduleYearMonth;
import com.joseyustiz.flightinterconnection.core.port.secondary.SchedulePort;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ScheduleHttpAdapter implements SchedulePort {
    private static final WebClient webClient = WebClient.builder().build();
    private final FlightScheduleMapper mapper;

    @Data
    public static class ScheduleDto {
        List<DayScheduleDto> days;
    }

    @Data
    public static class TimeScheduleDto {
        String departureTime;
        String arrivalTime;

    }

    @Data
    public static class DayScheduleDto {
        Integer day;
        List<TimeScheduleDto> flights;
    }


//    @Override
//    public Flux<FlightSchedule> getAvailableFlightsByDepartureAirportAndArrivalAirportAndScheduleYearMonth(AirportIataCode departureAirport, AirportIataCode arrivalAirport, ScheduleYearMonth yearMonth) {
//        return null;
//    }

    @Override
    public List<FlightSchedule> getAvailableFlightsByDepartureAirportAndArrivalAirportAndScheduleYearMonthAsList(AirportIataCode departureAirport,
                                                                                                                 AirportIataCode arrivalAirport, ScheduleYearMonth yearMonth) {
        final Function<UriBuilder, URI> url = uriBuilder -> uriBuilder
                .scheme("https")
                .host("timtbl-api.ryanair.com")
                .path("3/schedules/{departure}/{arrival}/years/{year}/months/{month}")
                .build(departureAirport.getValue(), arrivalAirport.getValue(),
                        yearMonth.getValue().getYear(), yearMonth.getValue().getMonthValue());

        return webClient.get().uri(url).retrieve()
                .onStatus(HttpStatus::isError,
                        clientResponse -> Mono.error(new IntegrationException(
                                String.format("Error calling url %s. HttpResponse: %s", url, clientResponse.statusCode()))))
                .bodyToMono(ScheduleDto.class)
                .map(dto -> mapper.toDomain(dto, departureAirport, arrivalAirport, yearMonth)).block(Duration.ofSeconds(10));
    }
}
