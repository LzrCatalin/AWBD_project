package com.awbd.airport_manager.service;

import com.awbd.airport_manager.dto.GateDto;
import com.awbd.airport_manager.exception.ResourceNotFoundException;
import com.awbd.airport_manager.mapper.GateMapper;
import com.awbd.airport_manager.model.Gate;
import com.awbd.airport_manager.repository.GateRepository;
import com.awbd.airport_manager.service.impl.GateServiceImpl;
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
class GateServiceImplTest {

    @Mock
    private GateRepository gateRepository;
    @Mock
    private GateMapper gateMapper;

    @InjectMocks
    private GateServiceImpl gateService;

    private UUID id;
    private Gate gate;
    private GateDto gateDto;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        gate = new Gate();
        gate.setId(id);
        gate.setGateNo("A1");
        gate.setTerminal("T1");

        gateDto = new GateDto();
        gateDto.setId(id);
        gateDto.setGateNo("A1");
        gateDto.setTerminal("T1");
    }

    @Test
    @SuppressWarnings("unchecked")
    void search_returnsPagedResponse() {
        SearchDTO searchDTO = new SearchDTO();
        Page<Gate> page = new PageImpl<>(List.of(gate));

        when(gateRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(gateMapper.toDto(gate)).thenReturn(gateDto);

        PagedResponse<GateDto> result = gateService.search(searchDTO);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalItems()).isEqualTo(1L);
    }

    @Test
    void getById_whenFound_returnsDto() {
        when(gateRepository.findById(id)).thenReturn(Optional.of(gate));
        when(gateMapper.toDto(gate)).thenReturn(gateDto);

        GateDto result = gateService.getById(id);

        assertThat(result.getGateNo()).isEqualTo("A1");
    }

    @Test
    void getById_whenNotFound_throwsResourceNotFoundException() {
        when(gateRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gateService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesAndReturnsDto() {
        when(gateMapper.toEntity(gateDto)).thenReturn(gate);
        when(gateRepository.save(gate)).thenReturn(gate);
        when(gateMapper.toDto(gate)).thenReturn(gateDto);

        GateDto result = gateService.create(gateDto);

        verify(gateRepository).save(gate);
        assertThat(result.getGateNo()).isEqualTo("A1");
    }

    @Test
    void update_whenFound_updatesFields() {
        gateDto.setGateNo("B2");
        gateDto.setTerminal("T2");

        when(gateRepository.findById(id)).thenReturn(Optional.of(gate));
        when(gateRepository.save(gate)).thenReturn(gate);
        when(gateMapper.toDto(gate)).thenReturn(gateDto);

        gateService.update(id, gateDto);

        assertThat(gate.getGateNo()).isEqualTo("B2");
        assertThat(gate.getTerminal()).isEqualTo("T2");
        verify(gateRepository).save(gate);
    }

    @Test
    void update_whenNotFound_throwsResourceNotFoundException() {
        when(gateRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gateService.update(id, gateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_whenFound_deletesGate() {
        when(gateRepository.findById(id)).thenReturn(Optional.of(gate));

        gateService.delete(id);

        verify(gateRepository).delete(gate);
    }

    @Test
    void delete_whenNotFound_throwsResourceNotFoundException() {
        when(gateRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gateService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
