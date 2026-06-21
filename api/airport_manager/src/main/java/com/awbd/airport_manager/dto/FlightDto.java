package com.awbd.airport_manager.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class FlightDto {

    private UUID id;

    @NotBlank(message = "The flight no. is mandatory")
    private String flightNo;

    @Future(message = "Departure time should be in the future")
    private LocalDateTime departureTime;

    @Future(message = "Arrival time should be in the future")
    private LocalDateTime arrivalTime;

    @NotBlank(message = "Departure city is mandatory")
    private String departureCity;

    @NotBlank(message = "Arrival city is mandatory")
    private String arrivalCity;

    @NotNull(message = "Aircraft is mandatory")
    private UUID aircraftId;

    private UUID gateId;

    private AircraftDto aircraft;
    private GateDto gate;
}
