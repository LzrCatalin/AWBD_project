package com.awbd.airport_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SeatDto {

    private UUID id;

    @NotBlank(message = "Seat number is mandatory")
    private String seatNo;

    private String type;
    private boolean available;

    @NotNull(message = "Flight is mandatory")
    private UUID flightId;
}
