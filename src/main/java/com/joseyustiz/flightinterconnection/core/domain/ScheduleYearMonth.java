package com.joseyustiz.flightinterconnection.core.domain;

import lombok.Value;

import java.io.Serializable;
import java.time.YearMonth;
@Value
public class ScheduleYearMonth implements Serializable {
    YearMonth value;
}
