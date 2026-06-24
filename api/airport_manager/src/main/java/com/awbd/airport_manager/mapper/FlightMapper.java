package com.awbd.airport_manager.mapper;

import com.awbd.airport_manager.dto.AircraftDto;
import com.awbd.airport_manager.dto.FlightDto;
import com.awbd.airport_manager.dto.GateDto;
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
        dto.setBaseFare(flight.getBaseFare());
        dto.setTaxes(flight.getTaxes());

        if (flight.getAircraft() != null) {
            dto.setAircraftId(flight.getAircraft().getId());
            AircraftDto aircraftDto = new AircraftDto();
            aircraftDto.setId(flight.getAircraft().getId());
            aircraftDto.setModel(flight.getAircraft().getModel());
            aircraftDto.setPlaneNo(flight.getAircraft().getPlaneNo());
            aircraftDto.setCapacity(flight.getAircraft().getCapacity());
            dto.setAircraft(aircraftDto);
        }
        if (flight.getGate() != null) {
            dto.setGateId(flight.getGate().getId());
            GateDto gateDto = new GateDto();
            gateDto.setId(flight.getGate().getId());
            gateDto.setGateNo(flight.getGate().getGateNo());
            gateDto.setTerminal(flight.getGate().getTerminal());
            dto.setGate(gateDto);
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
        flight.setBaseFare(dto.getBaseFare() != null ? dto.getBaseFare() : java.math.BigDecimal.ZERO);
        flight.setTaxes(dto.getTaxes() != null ? dto.getTaxes() : java.math.BigDecimal.ZERO);
        return flight;
    }
}
