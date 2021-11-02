package com.joseyustiz.flightinterconnection.infrastructure.secondary;

import com.joseyustiz.flightinterconnection.core.domain.Route;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RouteMapper {
    RouteMapper INSTANCE = Mappers.getMapper(RouteMapper.class);
    @Mapping(target = "departureAirport.value", source = "dto.airportFrom")
    @Mapping(target = "arrivalAirport.value", source = "dto.airportTo")
    Route toDomain(RouteHttpAdapter.RouteDto dto);

}
