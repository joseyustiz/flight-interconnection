package com.joseyustiz.flightinterconnection.infrastructure.primary.web;

import com.joseyustiz.flightinterconnection.core.domain.FlightSchedule;
import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto.FlightScheduleDto;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto.InterconnectedFlightDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper
public interface InterconnectedFlightMapper {
    InterconnectedFlightMapper INSTANCE = Mappers.getMapper(InterconnectedFlightMapper.class);

    InterconnectedFlightDto toInterconnectedFlightDto(InterconnectedFlight interconnectedFlight);

    @Mapping(source = "flightSchedule.departureAirport.value", target = "departureAirport")
    @Mapping(source = "flightSchedule.arrivalAirport.value", target = "arrivalAirport")
    @Mapping(source = "flightSchedule.departureDateTime.value", target = "departureDateTime")
    @Mapping(source = "flightSchedule.arrivalDateTime.value", target = "arrivalDateTime")
    FlightScheduleDto toFlightScheduleDto(FlightSchedule flightSchedule);
}
