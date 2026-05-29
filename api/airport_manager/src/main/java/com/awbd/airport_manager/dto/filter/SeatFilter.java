package com.awbd.airport_manager.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SeatFilter {

    private UUID flightId;
    private Boolean available;
    private String type;
}
