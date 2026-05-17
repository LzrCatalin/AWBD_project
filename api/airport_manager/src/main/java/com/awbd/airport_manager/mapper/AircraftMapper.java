package com.awbd.airport_manager.mapper;

import com.awbd.airport_manager.dto.AircraftDto;
import com.awbd.airport_manager.model.Aircraft;
import org.springframework.stereotype.Component;

@Component
public class AircraftMapper implements EntityMapper<Aircraft, AircraftDto> {

    @Override
    public AircraftDto toDto(Aircraft aircraft) {
        AircraftDto dto = new AircraftDto();
        dto.setId(aircraft.getId());
        dto.setModel(aircraft.getModel());
        dto.setCapacity(aircraft.getCapacity());
        dto.setPlaneNo(aircraft.getPlaneNo());
        return dto;
    }

    @Override
    public Aircraft toEntity(AircraftDto dto) {
        Aircraft aircraft = new Aircraft();
        aircraft.setModel(dto.getModel());
        aircraft.setCapacity(dto.getCapacity());
        aircraft.setPlaneNo(dto.getPlaneNo());
        return aircraft;
    }
}
