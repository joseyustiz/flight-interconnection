package com.joseyustiz.flightinterconnection.core.port.secondary;

import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.FlightSchedule;
import com.joseyustiz.flightinterconnection.core.domain.ScheduleYearMonth;
import com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase;

import java.util.List;

public interface SchedulePort {
//    Flux<FlightSchedule> getAvailableFlightsByDepartureAirportAndArrivalAirportAndScheduleYearMonth(AirportIataCode departureAirport, AirportIataCode arrivalAirport, ScheduleYearMonth yearMonth);
    List<FlightSchedule> getAvailableFlightsByDepartureAirportAndArrivalAirportAndScheduleYearMonthAsList(AirportIataCode departureAirport, AirportIataCode arrivalAirport, GetInterconnectedFlightUseCase.Query query, ScheduleYearMonth yearMonth);
    List<FlightSchedule> getAvailableFlightsByDepartureAirportAndArrivalAirportAndScheduleYearMonthAsList(GetInterconnectedFlightUseCase.Query query, ScheduleYearMonth yearMonth);
}
