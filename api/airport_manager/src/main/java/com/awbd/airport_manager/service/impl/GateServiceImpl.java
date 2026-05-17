package com.awbd.airport_manager.service.impl;

import com.awbd.airport_manager.dto.GateDto;
import com.awbd.airport_manager.exception.ResourceNotFoundException;
import com.awbd.airport_manager.mapper.GateMapper;
import com.awbd.airport_manager.model.Gate;
import com.awbd.airport_manager.repository.GateRepository;
import com.awbd.airport_manager.service.api.GateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GateServiceImpl implements GateService {

    private final GateRepository gateRepository;
    private final GateMapper gateMapper;

    @Override
    public List<GateDto> getAll() {
        return gateMapper.toList(gateRepository.findAll());
    }

    @Override
    public GateDto getById(UUID id) {
        return gateMapper.toDto(findById(id));
    }

    @Override
    @Transactional
    public GateDto create(GateDto dto) {
        return gateMapper.toDto(gateRepository.save(gateMapper.toEntity(dto)));
    }

    @Override
    @Transactional
    public GateDto update(UUID id, GateDto dto) {
        Gate gate = findById(id);
        gate.setGateNo(dto.getGateNo());
        gate.setTerminal(dto.getTerminal());
        return gateMapper.toDto(gateRepository.save(gate));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        gateRepository.delete(findById(id));
    }

    private Gate findById(UUID id) {
        return gateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gate", id));
    }
}
