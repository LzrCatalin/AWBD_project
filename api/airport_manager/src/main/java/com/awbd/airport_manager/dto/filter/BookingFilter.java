package com.awbd.airport_manager.dto.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BookingFilter {

    private UUID flightId;
    private UUID accountId;
}
