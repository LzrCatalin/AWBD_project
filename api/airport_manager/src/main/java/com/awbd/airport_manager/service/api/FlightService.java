package com.awbd.airport_manager.service.api;

import com.awbd.airport_manager.dto.FlightDto;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;

import java.util.UUID;

public interface FlightService {

    PagedResponse<FlightDto> search(SearchDTO searchDTO);

    FlightDto getById(UUID id);

    FlightDto create(FlightDto dto);

    FlightDto update(UUID id, FlightDto dto);

    void delete(UUID id);
}
