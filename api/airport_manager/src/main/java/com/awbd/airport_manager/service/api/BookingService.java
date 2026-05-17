package com.awbd.airport_manager.service.api;

import com.awbd.airport_manager.dto.BookingDto;

import java.util.List;
import java.util.UUID;

public interface BookingService {

    List<BookingDto> getAll();

    BookingDto getById(UUID id);

    BookingDto create(BookingDto dto);

    void delete(UUID id);
}
