package com.awbd.airport_manager.mapper;

import com.awbd.airport_manager.dto.BookingDto;
import com.awbd.airport_manager.model.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingMapper implements EntityMapper<Booking, BookingDto> {

    private final FlightMapper flightMapper;
    private final AccountMapper accountMapper;
    private final SeatMapper seatMapper;

    @Override
    public BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setBookingNo(booking.getBookingNo());
        dto.setBookingDate(booking.getBookingDate());
        dto.setTotalPrice(booking.getTotalPrice());

        if (booking.getFlight() != null) {
            dto.setFlightId(booking.getFlight().getId());
            dto.setFlight(flightMapper.toDto(booking.getFlight()));
        }
        if (booking.getAccount() != null) {
            dto.setAccountId(booking.getAccount().getId());
            dto.setAccount(accountMapper.toDto(booking.getAccount()));
        }
        if (booking.getSeat() != null) {
            dto.setSeatId(booking.getSeat().getId());
            dto.setSeat(seatMapper.toDto(booking.getSeat()));
        }

        return dto;
    }

    @Override
    public Booking toEntity(BookingDto dto) {
        Booking booking = new Booking();
        booking.setBookingNo(dto.getBookingNo());
        booking.setBookingDate(dto.getBookingDate());
        return booking;
    }
}
