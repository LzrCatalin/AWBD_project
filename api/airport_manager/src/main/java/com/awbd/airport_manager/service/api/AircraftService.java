package com.awbd.airport_manager.service.api;

import com.awbd.airport_manager.dto.AircraftDto;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;

import java.util.UUID;

public interface AircraftService {

    PagedResponse<AircraftDto> search(SearchDTO searchDTO);

    AircraftDto getById(UUID id);

    AircraftDto create(AircraftDto dto);

    AircraftDto update(UUID id, AircraftDto dto);

    void delete(UUID id);
}
