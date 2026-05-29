package com.awbd.airport_manager.service.impl;

import com.awbd.airport_manager.dto.BookingDto;
import com.awbd.airport_manager.exception.ResourceNotFoundException;
import com.awbd.airport_manager.mapper.BookingMapper;
import com.awbd.airport_manager.model.Booking;
import com.awbd.airport_manager.repository.AccountRepository;
import com.awbd.airport_manager.repository.BookingRepository;
import com.awbd.airport_manager.repository.FlightRepository;
import com.awbd.airport_manager.repository.SeatRepository;
import com.awbd.airport_manager.service.api.BookingService;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;
import com.awbd.airport_manager.util.spec.QuerySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final AccountRepository accountRepository;
    private final SeatRepository seatRepository;
    private final BookingMapper bookingMapper;

    @Override
    public PagedResponse<BookingDto> search(SearchDTO searchDTO) {
        return new PagedResponse<>(
                bookingRepository.findAll(new QuerySpecification<>(searchDTO), searchDTO.buildPageable())
                        .map(bookingMapper::toDto)
        );
    }

    @Override
    public BookingDto getById(UUID id) {
        return bookingMapper.toDto(findById(id));
    }

    @Override
    @Transactional
    public BookingDto create(BookingDto dto) {
        Booking booking = new Booking();
        booking.setBookingNo(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setBookingDate(LocalDateTime.now());

        booking.setFlight(flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight", dto.getFlightId())));

        booking.setAccount(accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", dto.getAccountId())));

        booking.setSeat(seatRepository.findById(dto.getSeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Seat", dto.getSeatId())));

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        bookingRepository.delete(findById(id));
    }

    private Booking findById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }
}
