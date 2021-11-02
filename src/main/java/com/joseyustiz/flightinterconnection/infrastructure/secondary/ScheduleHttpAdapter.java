package com.joseyustiz.flightinterconnection.infrastructure.secondary;

import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.FlightSchedule;
import com.joseyustiz.flightinterconnection.core.domain.ScheduleYearMonth;
import com.joseyustiz.flightinterconnection.core.port.secondary.SchedulePort;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ScheduleHttpAdapter implements SchedulePort {
//    @Override
//    public Flux<FlightSchedule> getAvailableFlightsByDepartureAirportAndArrivalAirportAndScheduleYearMonth(AirportIataCode departureAirport, AirportIataCode arrivalAirport, ScheduleYearMonth yearMonth) {
//        return null;
//    }

    @Override
    public List<FlightSchedule> getAvailableFlightsByDepartureAirportAndArrivalAirportAndScheduleYearMonthAsList(AirportIataCode departureAirport, AirportIataCode arrivalAirport, ScheduleYearMonth yearMonth) {
        return Collections.emptyList();
    }
}
