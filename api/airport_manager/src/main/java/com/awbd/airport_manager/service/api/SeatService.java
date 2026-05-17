package com.awbd.airport_manager.service.api;

import com.awbd.airport_manager.dto.SeatDto;

import java.util.List;
import java.util.UUID;

public interface SeatService {

    List<SeatDto> getAll();

    SeatDto getById(UUID id);

    SeatDto create(SeatDto dto);

    SeatDto update(UUID id, SeatDto dto);

    void delete(UUID id);
}
