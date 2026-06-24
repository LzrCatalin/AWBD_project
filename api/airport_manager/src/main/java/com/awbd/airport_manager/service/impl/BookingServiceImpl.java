package com.awbd.airport_manager.service.impl;

import com.awbd.airport_manager.dto.BookingDto;
import com.awbd.airport_manager.exception.ConflictException;
import com.awbd.airport_manager.exception.ResourceNotFoundException;
import com.awbd.airport_manager.mapper.BookingMapper;
import com.awbd.airport_manager.model.Booking;
import com.awbd.airport_manager.model.Flight;
import com.awbd.airport_manager.model.Seat;
import com.awbd.airport_manager.repository.BookingRepository;
import com.awbd.airport_manager.repository.FlightRepository;
import com.awbd.airport_manager.repository.SeatRepository;
import com.awbd.airport_manager.service.api.BookingService;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;
import com.awbd.airport_manager.util.search.enums.FilterOperation;
import com.awbd.airport_manager.util.security.SecurityUtils;
import com.awbd.airport_manager.util.spec.QuerySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;
    private final BookingMapper bookingMapper;
    private final AccountProvisioningService accountProvisioningService;

    @Override
    public PagedResponse<BookingDto> search(SearchDTO searchDTO) {
        if (!SecurityUtils.hasAnyRole("ROLE_ADMIN", "ROLE_STAFF")) {
            String email = SecurityUtils.getCurrentUserInfo().getEmail();
            searchDTO.addFilter("account.email", FilterOperation.EQ, email);
        }
        return new PagedResponse<>(
                bookingRepository.findAll(new QuerySpecification<>(searchDTO), searchDTO.buildPageable())
                        .map(bookingMapper::toDto)
        );
    }

    @Override
    public BookingDto getById(UUID id) {
        return bookingMapper.toDto(findById(id));
    }

    private static final BigDecimal CHECKED_BAG_PRICE = new BigDecimal("34");

    @Override
    @Transactional
    public BookingDto create(BookingDto dto) {
        Booking booking = new Booking();
        booking.setBookingNo(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setBookingDate(LocalDateTime.now());

        Flight flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight", dto.getFlightId()));
        booking.setFlight(flight);

        booking.setAccount(accountProvisioningService.resolveCurrentAccount());

        Seat seat = resolveSeat(dto, flight.getId());
        seat.setAvailable(false);
        seatRepository.save(seat);
        booking.setSeat(seat);

        booking.setTotalPrice(computeTotal(flight, dto.isAddCheckedBag()));

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Booking booking = findById(id);
        // Release the seat back to the available pool when a booking is cancelled.
        Seat seat = booking.getSeat();
        if (seat != null) {
            seat.setAvailable(true);
            seatRepository.save(seat);
        }
        bookingRepository.delete(booking);
    }

    /** Use the explicitly chosen seat, or auto-assign the first available seat on the flight. */
    private Seat resolveSeat(BookingDto dto, UUID flightId) {
        if (dto.getSeatId() != null) {
            return seatRepository.findById(dto.getSeatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seat", dto.getSeatId()));
        }
        return seatRepository.findByFlight_IdAndIsAvailableTrue(flightId).stream()
                .findFirst()
                .orElseThrow(() -> new ConflictException("No available seats for this flight"));
    }

    private BigDecimal computeTotal(Flight flight, boolean addCheckedBag) {
        BigDecimal total = nz(flight.getBaseFare()).add(nz(flight.getTaxes()));
        if (addCheckedBag) {
            total = total.add(CHECKED_BAG_PRICE);
        }
        return total;
    }

    private BigDecimal nz(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private Booking findById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }
}
