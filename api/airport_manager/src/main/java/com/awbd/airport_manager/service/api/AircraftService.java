package com.awbd.airport_manager.service.api;

import com.awbd.airport_manager.dto.AircraftDto;

import java.util.List;
import java.util.UUID;

public interface AircraftService {

    List<AircraftDto> getAll();

    AircraftDto getById(UUID id);

    AircraftDto create(AircraftDto dto);

    AircraftDto update(UUID id, AircraftDto dto);

    void delete(UUID id);
}
