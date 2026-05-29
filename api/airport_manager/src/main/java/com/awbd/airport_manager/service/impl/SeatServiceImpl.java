package com.awbd.airport_manager.service.impl;

import com.awbd.airport_manager.dto.SeatDto;
import com.awbd.airport_manager.exception.ResourceNotFoundException;
import com.awbd.airport_manager.mapper.SeatMapper;
import com.awbd.airport_manager.model.Seat;
import com.awbd.airport_manager.repository.FlightRepository;
import com.awbd.airport_manager.repository.SeatRepository;
import com.awbd.airport_manager.service.api.SeatService;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;
import com.awbd.airport_manager.util.spec.QuerySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final FlightRepository flightRepository;
    private final SeatMapper seatMapper;

    @Override
    public PagedResponse<SeatDto> search(SearchDTO searchDTO) {
        return new PagedResponse<>(
                seatRepository.findAll(new QuerySpecification<>(searchDTO), searchDTO.buildPageable())
                        .map(seatMapper::toDto)
        );
    }

    @Override
    public SeatDto getById(UUID id) {
        return seatMapper.toDto(findById(id));
    }

    @Override
    @Transactional
    public SeatDto create(SeatDto dto) {
        Seat seat = seatMapper.toEntity(dto);
        resolveRelations(seat, dto);
        return seatMapper.toDto(seatRepository.save(seat));
    }

    @Override
    @Transactional
    public SeatDto update(UUID id, SeatDto dto) {
        Seat seat = findById(id);
        seat.setSeatNo(dto.getSeatNo());
        seat.setType(dto.getType());
        seat.setAvailable(dto.isAvailable());
        resolveRelations(seat, dto);
        return seatMapper.toDto(seatRepository.save(seat));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        seatRepository.delete(findById(id));
    }

    private void resolveRelations(Seat seat, SeatDto dto) {
        seat.setFlight(flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight", dto.getFlightId())));
    }

    private Seat findById(UUID id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seat", id));
    }
}
