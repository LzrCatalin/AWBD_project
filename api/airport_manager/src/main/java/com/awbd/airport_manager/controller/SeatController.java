package com.awbd.airport_manager.controller;

import com.awbd.airport_manager.dto.SeatDto;
import com.awbd.airport_manager.service.api.SeatService;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.awbd.airport_manager.util.enums.ApiPaths.Seats;

@RestController
@RequestMapping(Seats)
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping("/search")
    @Operation(summary = "Search seats with filters, sorters and pagination")
    public ResponseEntity<PagedResponse<SeatDto>> search(@RequestBody SearchDTO searchDTO) {
        return ResponseEntity.ok(seatService.search(searchDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get seat by id")
    public ResponseEntity<SeatDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(seatService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create seat")
    public ResponseEntity<SeatDto> create(@Valid @RequestBody SeatDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(seatService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update seat")
    public ResponseEntity<SeatDto> update(@PathVariable UUID id, @Valid @RequestBody SeatDto dto) {
        return ResponseEntity.ok(seatService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete seat")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        seatService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
