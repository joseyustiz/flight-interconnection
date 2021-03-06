package com.joseyustiz.flightinterconnection.infrastructure.secondary;

import com.joseyustiz.flightinterconnection.core.domain.AirportIataCode;
import com.joseyustiz.flightinterconnection.core.domain.FlightDateTime;
import com.joseyustiz.flightinterconnection.core.domain.FlightSchedule;
import com.joseyustiz.flightinterconnection.core.domain.ScheduleYearMonth;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class FlightScheduleMapperImpl implements FlightScheduleMapper {
    @Override
    public List<FlightSchedule> toDomain(ScheduleHttpAdapter.ScheduleDto dto,
                                         @NonNull AirportIataCode departure, @NonNull AirportIataCode arrival, @NonNull ScheduleYearMonth yearMonth) {
        if(dto == null){
            return  null;
        }
        List<FlightSchedule> result = new ArrayList<>();

        if (dto.days == null){
            return result;
        }

        dto.days.forEach(daySchedule -> {
            for (ScheduleHttpAdapter.TimeScheduleDto timeSchedule : daySchedule.flights) {

                final var departureTime = LocalTime.parse(timeSchedule.getDepartureTime());
                final var arrivalTime = LocalTime.parse(timeSchedule.getArrivalTime());

                final var departureDateTime = new FlightDateTime(LocalDateTime.of(yearMonth.getValue().getYear(),
                        yearMonth.getValue().getMonth(), daySchedule.day, departureTime.getHour(),
                        departureTime.getMinute()));
                var additionalDay = 0;
                if(arrivalTime.isBefore(departureTime)){
                    additionalDay++;
                }
                final var arrivalDateTime =  new FlightDateTime(LocalDateTime.of(yearMonth.getValue().getYear(),
                        yearMonth.getValue().getMonth(), daySchedule.day, arrivalTime.getHour(), arrivalTime.getMinute()).plusDays(additionalDay));
                result.add(FlightSchedule.builder()
                        .departureAirport(departure)
                        .arrivalAirport(arrival)
                        .departureDateTime(departureDateTime)
                        .arrivalDateTime(arrivalDateTime)
                        .build());
            }
        });

        return result;
    }
}
