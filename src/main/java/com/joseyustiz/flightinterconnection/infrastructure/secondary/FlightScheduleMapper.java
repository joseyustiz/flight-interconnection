package com.joseyustiz.flightinterconnection.infrastructure.secondary;

import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.FlightSchedule;
import com.joseyustiz.flightinterconnection.core.domain.ScheduleYearMonth;

import java.util.List;

public interface FlightScheduleMapper {
    List<FlightSchedule> toDomain(ScheduleHttpAdapter.ScheduleDto dto, AirportIataCode departure, AirportIataCode arrival, ScheduleYearMonth yearMonth);
}
