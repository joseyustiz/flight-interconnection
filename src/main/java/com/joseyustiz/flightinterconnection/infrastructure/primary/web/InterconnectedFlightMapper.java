package com.joseyustiz.flightinterconnection.infrastructure.primary.web;

import com.joseyustiz.flightinterconnection.core.domain.InterconnectedFlight;
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto.InterconnectedFlightDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface InterconnectedFlightMapper {
    InterconnectedFlightMapper INSTANCE = Mappers.getMapper(InterconnectedFlightMapper.class);

    InterconnectedFlightDto toDto(InterconnectedFlight interconnectedFlight);
}
