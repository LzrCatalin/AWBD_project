package com.awbd.airport_manager.service;

import com.awbd.airport_manager.dto.FlightDto;
import com.awbd.airport_manager.exception.ResourceNotFoundException;
import com.awbd.airport_manager.mapper.FlightMapper;
import com.awbd.airport_manager.model.Aircraft;
import com.awbd.airport_manager.model.Flight;
import com.awbd.airport_manager.model.Gate;
import com.awbd.airport_manager.repository.AircraftRepository;
import com.awbd.airport_manager.repository.FlightRepository;
import com.awbd.airport_manager.repository.GateRepository;
import com.awbd.airport_manager.service.impl.FlightServiceImpl;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;
    @Mock
    private AircraftRepository aircraftRepository;
    @Mock
    private GateRepository gateRepository;
    @Mock
    private FlightMapper flightMapper;

    @InjectMocks
    private FlightServiceImpl flightService;

    private UUID id;
    private UUID aircraftId;
    private UUID gateId;
    private Flight flight;
    private FlightDto flightDto;
    private Aircraft aircraft;
    private Gate gate;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        aircraftId = UUID.randomUUID();
        gateId = UUID.randomUUID();

        aircraft = new Aircraft();
        aircraft.setId(aircraftId);
        aircraft.setModel("Boeing 737");

        gate = new Gate();
        gate.setId(gateId);
        gate.setGateNo("A1");

        flight = new Flight();
        flight.setId(id);
        flight.setFlightNo("RO100");
        flight.setDepartureCity("Bucharest");
        flight.setArrivalCity("London");
        flight.setDepartureTime(LocalDateTime.now().plusDays(1));
        flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(3));
        flight.setAircraft(aircraft);

        flightDto = new FlightDto();
        flightDto.setId(id);
        flightDto.setFlightNo("RO100");
        flightDto.setDepartureCity("Bucharest");
        flightDto.setArrivalCity("London");
        flightDto.setDepartureTime(LocalDateTime.now().plusDays(1));
        flightDto.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(3));
        flightDto.setAircraftId(aircraftId);
        flightDto.setGateId(gateId);
    }

    @Test
    @SuppressWarnings("unchecked")
    void search_returnsPagedResponse() {
        SearchDTO searchDTO = new SearchDTO();
        Page<Flight> page = new PageImpl<>(List.of(flight));

        when(flightRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        PagedResponse<FlightDto> result = flightService.search(searchDTO);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalItems()).isEqualTo(1L);
    }

    @Test
    void getById_whenFound_returnsDto() {
        when(flightRepository.findById(id)).thenReturn(Optional.of(flight));
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        FlightDto result = flightService.getById(id);

        assertThat(result.getFlightNo()).isEqualTo("RO100");
    }

    @Test
    void getById_whenNotFound_throwsResourceNotFoundException() {
        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_withAircraftAndGate_savesAndReturnsDto() {
        when(flightMapper.toEntity(flightDto)).thenReturn(flight);
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(aircraft));
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        FlightDto result = flightService.create(flightDto);

        verify(flightRepository).save(flight);
        assertThat(result.getFlightNo()).isEqualTo("RO100");
    }

    @Test
    void create_withNullGate_savesFlightWithoutGate() {
        flightDto.setGateId(null);

        when(flightMapper.toEntity(flightDto)).thenReturn(flight);
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(aircraft));
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.create(flightDto);

        verify(gateRepository, never()).findById(any());
        assertThat(flight.getGate()).isNull();
    }

    @Test
    void create_whenAircraftNotFound_throwsResourceNotFoundException() {
        when(flightMapper.toEntity(flightDto)).thenReturn(flight);
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.create(flightDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_whenGateNotFound_throwsResourceNotFoundException() {
        when(flightMapper.toEntity(flightDto)).thenReturn(flight);
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(aircraft));
        when(gateRepository.findById(gateId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.create(flightDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_whenFound_updatesAndReturnsDto() {
        flightDto.setFlightNo("RO200");
        flightDto.setDepartureCity("Cluj");

        when(flightRepository.findById(id)).thenReturn(Optional.of(flight));
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(aircraft));
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(flightRepository.save(flight)).thenReturn(flight);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);

        flightService.update(id, flightDto);

        assertThat(flight.getFlightNo()).isEqualTo("RO200");
        assertThat(flight.getDepartureCity()).isEqualTo("Cluj");
        verify(flightRepository).save(flight);
    }

    @Test
    void update_whenNotFound_throwsResourceNotFoundException() {
        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.update(id, flightDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_whenFound_deletesFlight() {
        when(flightRepository.findById(id)).thenReturn(Optional.of(flight));

        flightService.delete(id);

        verify(flightRepository).delete(flight);
    }

    @Test
    void delete_whenNotFound_throwsResourceNotFoundException() {
        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> flightService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
