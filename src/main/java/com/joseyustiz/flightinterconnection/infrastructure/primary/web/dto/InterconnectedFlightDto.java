package com.joseyustiz.flightinterconnection.infrastructure.primary.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterconnectedFlightDto {
    private int stops;
}
