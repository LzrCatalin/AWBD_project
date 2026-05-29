package com.awbd.airport_manager.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FlightFilter {

    private String departureCity;
    private String arrivalCity;
    private LocalDateTime departureAfter;
    private LocalDateTime departureBefore;
}
