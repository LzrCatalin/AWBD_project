package com.awbd.airport_manager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GateDto {

    private UUID id;

    @NotBlank(message = "Gate number is mandatory")
    private String gateNo;

    private String terminal;
}
