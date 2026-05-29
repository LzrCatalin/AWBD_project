package com.awbd.airport_manager.service.api;

import com.awbd.airport_manager.dto.SeatDto;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;

import java.util.UUID;

public interface SeatService {

    PagedResponse<SeatDto> search(SearchDTO searchDTO);

    SeatDto getById(UUID id);

    SeatDto create(SeatDto dto);

    SeatDto update(UUID id, SeatDto dto);

    void delete(UUID id);
}
