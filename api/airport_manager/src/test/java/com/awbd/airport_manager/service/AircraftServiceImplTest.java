package com.awbd.airport_manager.service;

import com.awbd.airport_manager.dto.AircraftDto;
import com.awbd.airport_manager.exception.ResourceNotFoundException;
import com.awbd.airport_manager.mapper.AircraftMapper;
import com.awbd.airport_manager.model.Aircraft;
import com.awbd.airport_manager.repository.AircraftRepository;
import com.awbd.airport_manager.service.impl.AircraftServiceImpl;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AircraftServiceImplTest {

    @Mock
    private AircraftRepository aircraftRepository;
    @Mock
    private AircraftMapper aircraftMapper;

    @InjectMocks
    private AircraftServiceImpl aircraftService;

    private UUID id;
    private Aircraft aircraft;
    private AircraftDto aircraftDto;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        aircraft = new Aircraft();
        aircraft.setId(id);
        aircraft.setModel("Boeing 737");
        aircraft.setCapacity(180);
        aircraft.setPlaneNo("YR-BGA");

        aircraftDto = new AircraftDto();
        aircraftDto.setId(id);
        aircraftDto.setModel("Boeing 737");
        aircraftDto.setCapacity(180);
        aircraftDto.setPlaneNo("YR-BGA");
    }

    @Test
    @SuppressWarnings("unchecked")
    void search_returnsPagedResponse() {
        SearchDTO searchDTO = new SearchDTO();
        Page<Aircraft> page = new PageImpl<>(List.of(aircraft));

        when(aircraftRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(aircraftMapper.toDto(aircraft)).thenReturn(aircraftDto);

        PagedResponse<AircraftDto> result = aircraftService.search(searchDTO);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalItems()).isEqualTo(1L);
    }

    @Test
    void getById_whenFound_returnsDto() {
        when(aircraftRepository.findById(id)).thenReturn(Optional.of(aircraft));
        when(aircraftMapper.toDto(aircraft)).thenReturn(aircraftDto);

        AircraftDto result = aircraftService.getById(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getModel()).isEqualTo("Boeing 737");
    }

    @Test
    void getById_whenNotFound_throwsResourceNotFoundException() {
        when(aircraftRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aircraftService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesAndReturnsDto() {
        when(aircraftMapper.toEntity(aircraftDto)).thenReturn(aircraft);
        when(aircraftRepository.save(aircraft)).thenReturn(aircraft);
        when(aircraftMapper.toDto(aircraft)).thenReturn(aircraftDto);

        AircraftDto result = aircraftService.create(aircraftDto);

        verify(aircraftRepository).save(aircraft);
        assertThat(result.getModel()).isEqualTo("Boeing 737");
    }

    @Test
    void update_whenFound_updatesFields() {
        aircraftDto.setModel("Airbus A320");
        aircraftDto.setCapacity(150);
        aircraftDto.setPlaneNo("YR-ABC");

        when(aircraftRepository.findById(id)).thenReturn(Optional.of(aircraft));
        when(aircraftRepository.save(aircraft)).thenReturn(aircraft);
        when(aircraftMapper.toDto(aircraft)).thenReturn(aircraftDto);

        AircraftDto result = aircraftService.update(id, aircraftDto);

        verify(aircraftRepository).save(aircraft);
        assertThat(aircraft.getModel()).isEqualTo("Airbus A320");
        assertThat(aircraft.getCapacity()).isEqualTo(150);
    }

    @Test
    void update_whenNotFound_throwsResourceNotFoundException() {
        when(aircraftRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aircraftService.update(id, aircraftDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_whenFound_deletesAircraft() {
        when(aircraftRepository.findById(id)).thenReturn(Optional.of(aircraft));

        aircraftService.delete(id);

        verify(aircraftRepository).delete(aircraft);
    }

    @Test
    void delete_whenNotFound_throwsResourceNotFoundException() {
        when(aircraftRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aircraftService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
