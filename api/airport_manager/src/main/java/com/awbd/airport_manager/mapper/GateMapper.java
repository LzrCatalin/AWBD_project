package com.awbd.airport_manager.mapper;

import com.awbd.airport_manager.dto.GateDto;
import com.awbd.airport_manager.model.Gate;
import org.springframework.stereotype.Component;

@Component
public class GateMapper implements EntityMapper<Gate, GateDto> {

    @Override
    public GateDto toDto(Gate gate) {
        GateDto dto = new GateDto();
        dto.setId(gate.getId());
        dto.setGateNo(gate.getGateNo());
        dto.setTerminal(gate.getTerminal());
        return dto;
    }

    @Override
    public Gate toEntity(GateDto dto) {
        Gate gate = new Gate();
        gate.setGateNo(dto.getGateNo());
        gate.setTerminal(dto.getTerminal());
        return gate;
    }
}
