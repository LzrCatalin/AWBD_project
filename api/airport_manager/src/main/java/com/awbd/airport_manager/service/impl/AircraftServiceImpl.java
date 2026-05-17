package com.awbd.airport_manager.service.impl;

import com.awbd.airport_manager.dto.AircraftDto;
import com.awbd.airport_manager.exception.ResourceNotFoundException;
import com.awbd.airport_manager.mapper.AircraftMapper;
import com.awbd.airport_manager.model.Aircraft;
import com.awbd.airport_manager.repository.AircraftRepository;
import com.awbd.airport_manager.service.api.AircraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AircraftServiceImpl implements AircraftService {

    private final AircraftRepository aircraftRepository;
    private final AircraftMapper aircraftMapper;

    @Override
    public List<AircraftDto> getAll() {
        return aircraftMapper.toList(aircraftRepository.findAll());
    }

    @Override
    public AircraftDto getById(UUID id) {
        return aircraftMapper.toDto(findById(id));
    }

    @Override
    @Transactional
    public AircraftDto create(AircraftDto dto) {
        return aircraftMapper.toDto(aircraftRepository.save(aircraftMapper.toEntity(dto)));
    }

    @Override
    @Transactional
    public AircraftDto update(UUID id, AircraftDto dto) {
        Aircraft aircraft = findById(id);
        aircraft.setModel(dto.getModel());
        aircraft.setCapacity(dto.getCapacity());
        aircraft.setPlaneNo(dto.getPlaneNo());
        return aircraftMapper.toDto(aircraftRepository.save(aircraft));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        aircraftRepository.delete(findById(id));
    }

    private Aircraft findById(UUID id) {
        return aircraftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft", id));
    }
}
