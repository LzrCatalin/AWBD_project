package com.awbd.airport_manager.mapper;

import com.awbd.airport_manager.dto.BookingDto;
import com.awbd.airport_manager.model.Booking;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper implements EntityMapper<Booking, BookingDto> {

    @Override
    public BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setBookingNo(booking.getBookingNo());
        dto.setBookingDate(booking.getBookingDate());

        if (booking.getFlight() != null) {
            dto.setFlightId(booking.getFlight().getId());
        }
        if (booking.getAccount() != null) {
            dto.setAccountId(booking.getAccount().getId());
        }
        if (booking.getSeat() != null) {
            dto.setSeatId(booking.getSeat().getId());
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
