package com.awbd.airport_manager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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

    // Optional — when omitted, the service auto-assigns an available seat for the flight.
    private UUID seatId;

    // Optional checkout add-on (checked bag).
    private boolean addCheckedBag;

    private BigDecimal totalPrice;

    private LocalDateTime bookingDate;

    private FlightDto flight;
    private AccountDto account;
    private SeatDto seat;
}
