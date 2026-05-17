package com.awbd.airport_manager.mapper;

import com.awbd.airport_manager.dto.FlightDto;
import com.awbd.airport_manager.model.Flight;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper implements EntityMapper<Flight, FlightDto> {

    @Override
    public FlightDto toDto(Flight flight) {
        FlightDto dto = new FlightDto();
        dto.setId(flight.getId());
        dto.setFlightNo(flight.getFlightNo());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setDepartureCity(flight.getDepartureCity());
        dto.setArrivalCity(flight.getArrivalCity());

        if (flight.getAircraft() != null) {
            dto.setAircraftId(flight.getAircraft().getId());
        }
        if (flight.getGate() != null) {
            dto.setGateId(flight.getGate().getId());
        }

        return dto;
    }

    @Override
    public Flight toEntity(FlightDto dto) {
        Flight flight = new Flight();
        flight.setFlightNo(dto.getFlightNo());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setArrivalTime(dto.getArrivalTime());
        flight.setDepartureCity(dto.getDepartureCity());
        flight.setArrivalCity(dto.getArrivalCity());
        return flight;
    }
}
