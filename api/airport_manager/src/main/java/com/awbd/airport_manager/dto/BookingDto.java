package com.awbd.airport_manager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class BookingDto {

    private UUID id;
    private String bookingNo;

    @NotNull(message = "Flight is mandatory")
    private UUID flightId;

    private UUID accountId;

    @NotNull(message = "Seat is mandatory")
    private UUID seatId;

    private LocalDateTime bookingDate;

    private FlightDto flight;
    private AccountDto account;
    private SeatDto seat;
}
