package com.awbd.airport_manager.mapper;

import com.awbd.airport_manager.dto.SeatDto;
import com.awbd.airport_manager.model.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatMapper implements EntityMapper<Seat, SeatDto> {

    private final FlightMapper flightMapper;

    @Override
    public SeatDto toDto(Seat seat) {
        SeatDto dto = new SeatDto();
        dto.setId(seat.getId());
        dto.setSeatNo(seat.getSeatNo());
        dto.setType(seat.getType());
        dto.setAvailable(seat.isAvailable());

        if (seat.getFlight() != null) {
            dto.setFlightId(seat.getFlight().getId());
            dto.setFlight(flightMapper.toDto(seat.getFlight()));
        }

        return dto;
    }

    @Override
    public Seat toEntity(SeatDto dto) {
        Seat seat = new Seat();
        seat.setSeatNo(dto.getSeatNo());
        seat.setType(dto.getType());
        seat.setAvailable(dto.isAvailable());
        return seat;
    }
}
