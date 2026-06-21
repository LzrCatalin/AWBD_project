package com.awbd.airport_manager.service;

import com.awbd.airport_manager.dto.SeatDto;
import com.awbd.airport_manager.exception.ResourceNotFoundException;
import com.awbd.airport_manager.mapper.SeatMapper;
import com.awbd.airport_manager.model.Flight;
import com.awbd.airport_manager.model.Seat;
import com.awbd.airport_manager.repository.FlightRepository;
import com.awbd.airport_manager.repository.SeatRepository;
import com.awbd.airport_manager.service.impl.SeatServiceImpl;
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
class SeatServiceImplTest {

    @Mock
    private SeatRepository seatRepository;
    @Mock
    private FlightRepository flightRepository;
    @Mock
    private SeatMapper seatMapper;

    @InjectMocks
    private SeatServiceImpl seatService;

    private UUID id;
    private UUID flightId;
    private Seat seat;
    private SeatDto seatDto;
    private Flight flight;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        flightId = UUID.randomUUID();

        flight = new Flight();
        flight.setId(flightId);
        flight.setFlightNo("RO100");

        seat = new Seat();
        seat.setId(id);
        seat.setSeatNo("12A");
        seat.setType("ECONOMY");
        seat.setAvailable(true);
        seat.setFlight(flight);

        seatDto = new SeatDto();
        seatDto.setId(id);
        seatDto.setSeatNo("12A");
        seatDto.setType("ECONOMY");
        seatDto.setAvailable(true);
        seatDto.setFlightId(flightId);
    }

    @Test
    @SuppressWarnings("unchecked")
    void search_returnsPagedResponse() {
        SearchDTO searchDTO = new SearchDTO();
        Page<Seat> page = new PageImpl<>(List.of(seat));

        when(seatRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(seatMapper.toDto(seat)).thenReturn(seatDto);

        PagedResponse<SeatDto> result = seatService.search(searchDTO);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalItems()).isEqualTo(1L);
    }

    @Test
    void getById_whenFound_returnsDto() {
        when(seatRepository.findById(id)).thenReturn(Optional.of(seat));
        when(seatMapper.toDto(seat)).thenReturn(seatDto);

        SeatDto result = seatService.getById(id);

        assertThat(result.getSeatNo()).isEqualTo("12A");
    }

    @Test
    void getById_whenNotFound_throwsResourceNotFoundException() {
        when(seatRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesAndReturnsDto() {
        when(seatMapper.toEntity(seatDto)).thenReturn(seat);
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flight));
        when(seatRepository.save(seat)).thenReturn(seat);
        when(seatMapper.toDto(seat)).thenReturn(seatDto);

        SeatDto result = seatService.create(seatDto);

        verify(seatRepository).save(seat);
        assertThat(result.getSeatNo()).isEqualTo("12A");
    }

    @Test
    void create_whenFlightNotFound_throwsResourceNotFoundException() {
        when(seatMapper.toEntity(seatDto)).thenReturn(seat);
        when(flightRepository.findById(flightId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.create(seatDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_whenFound_updatesFields() {
        seatDto.setSeatNo("15B");
        seatDto.setType("BUSINESS");
        seatDto.setAvailable(false);

        when(seatRepository.findById(id)).thenReturn(Optional.of(seat));
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flight));
        when(seatRepository.save(seat)).thenReturn(seat);
        when(seatMapper.toDto(seat)).thenReturn(seatDto);

        seatService.update(id, seatDto);

        assertThat(seat.getSeatNo()).isEqualTo("15B");
        assertThat(seat.getType()).isEqualTo("BUSINESS");
        assertThat(seat.isAvailable()).isFalse();
        verify(seatRepository).save(seat);
    }

    @Test
    void update_whenNotFound_throwsResourceNotFoundException() {
        when(seatRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.update(id, seatDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_whenFound_deletesSeat() {
        when(seatRepository.findById(id)).thenReturn(Optional.of(seat));

        seatService.delete(id);

        verify(seatRepository).delete(seat);
    }

    @Test
    void delete_whenNotFound_throwsResourceNotFoundException() {
        when(seatRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
