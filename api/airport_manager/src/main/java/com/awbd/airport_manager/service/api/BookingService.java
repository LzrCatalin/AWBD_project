package com.awbd.airport_manager.service.api;

import com.awbd.airport_manager.dto.BookingDto;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;

import java.util.UUID;

public interface BookingService {

    PagedResponse<BookingDto> search(SearchDTO searchDTO);

    BookingDto getById(UUID id);

    BookingDto create(BookingDto dto);

    void delete(UUID id);
}
