package com.awbd.airport_manager.service.api;

import com.awbd.airport_manager.dto.GateDto;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;

import java.util.UUID;

public interface GateService {

    PagedResponse<GateDto> search(SearchDTO searchDTO);

    GateDto getById(UUID id);

    GateDto create(GateDto dto);

    GateDto update(UUID id, GateDto dto);

    void delete(UUID id);
}
