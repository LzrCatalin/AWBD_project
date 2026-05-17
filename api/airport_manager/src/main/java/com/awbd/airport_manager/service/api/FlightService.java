package com.awbd.airport_manager.service.api;

import com.awbd.airport_manager.dto.FlightDto;

import java.util.List;
import java.util.UUID;

public interface FlightService {

    List<FlightDto> getAll();

    FlightDto getById(UUID id);

    FlightDto create(FlightDto dto);

    FlightDto update(UUID id, FlightDto dto);

    void delete(UUID id);
}
