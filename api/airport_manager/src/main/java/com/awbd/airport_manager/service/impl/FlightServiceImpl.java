package com.awbd.airport_manager.service.impl;

import com.awbd.airport_manager.dto.FlightDto;
import com.awbd.airport_manager.exception.ResourceNotFoundException;
import com.awbd.airport_manager.mapper.FlightMapper;
import com.awbd.airport_manager.model.Flight;
import com.awbd.airport_manager.repository.AircraftRepository;
import com.awbd.airport_manager.repository.FlightRepository;
import com.awbd.airport_manager.repository.GateRepository;
import com.awbd.airport_manager.service.api.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;
    private final GateRepository gateRepository;
    private final FlightMapper flightMapper;

    @Override
    public List<FlightDto> getAll() {
        return flightMapper.toList(flightRepository.findAll());
    }

    @Override
    public FlightDto getById(UUID id) {
        return flightMapper.toDto(findById(id));
    }

    @Override
    @Transactional
    public FlightDto create(FlightDto dto) {
        Flight flight = flightMapper.toEntity(dto);
        resolveRelations(flight, dto);
        return flightMapper.toDto(flightRepository.save(flight));
    }

    @Override
    @Transactional
    public FlightDto update(UUID id, FlightDto dto) {
        Flight flight = findById(id);
        flight.setFlightNo(dto.getFlightNo());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setArrivalTime(dto.getArrivalTime());
        flight.setDepartureCity(dto.getDepartureCity());
        flight.setArrivalCity(dto.getArrivalCity());
        resolveRelations(flight, dto);
        return flightMapper.toDto(flightRepository.save(flight));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        flightRepository.delete(findById(id));
    }

    private void resolveRelations(Flight flight, FlightDto dto) {
        flight.setAircraft(aircraftRepository.findById(dto.getAircraftId())
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft", dto.getAircraftId())));

        if (dto.getGateId() != null) {
            flight.setGate(gateRepository.findById(dto.getGateId())
                    .orElseThrow(() -> new ResourceNotFoundException("Gate", dto.getGateId())));
        } else {
            flight.setGate(null);
        }
    }

    private Flight findById(UUID id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight", id));
    }
}
