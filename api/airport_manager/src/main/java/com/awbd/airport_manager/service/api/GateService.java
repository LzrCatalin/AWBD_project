package com.awbd.airport_manager.service.api;

import com.awbd.airport_manager.dto.GateDto;

import java.util.List;
import java.util.UUID;

public interface GateService {

    List<GateDto> getAll();

    GateDto getById(UUID id);

    GateDto create(GateDto dto);

    GateDto update(UUID id, GateDto dto);

    void delete(UUID id);
}
