package com.awbd.airport_manager.controller;

import com.awbd.airport_manager.dto.SeatDto;
import com.awbd.airport_manager.service.api.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.awbd.airport_manager.util.enums.ApiPaths.Seats;

@RestController
@RequestMapping(Seats)
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    @Operation(summary = "Get all seats")
    public ResponseEntity<List<SeatDto>> getAll() {
        return ResponseEntity.ok(seatService.getAll());
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
