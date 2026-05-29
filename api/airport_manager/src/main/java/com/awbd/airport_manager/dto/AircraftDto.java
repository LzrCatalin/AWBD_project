package com.awbd.airport_manager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AircraftDto {

    private UUID id;

    @NotBlank(message = "Model is mandatory")
    private String model;

    @Min(value = 1, message = "Capacity needs to be at least 1")
    private int capacity;

    private String planeNo;
}
