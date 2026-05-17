package com.awbd.airport_manager.controller;

import com.awbd.airport_manager.dto.AircraftDto;
import com.awbd.airport_manager.service.api.AircraftService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.awbd.airport_manager.util.enums.ApiPaths.Aircrafts;

@RestController
@RequestMapping(Aircrafts)
@RequiredArgsConstructor
public class AircraftController {

    private final AircraftService aircraftService;

    @GetMapping
    @Operation(summary = "Get all aircrafts")
    public ResponseEntity<List<AircraftDto>> getAll() {
        return ResponseEntity.ok(aircraftService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get aircraft by id")
    public ResponseEntity<AircraftDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(aircraftService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create aircraft")
    public ResponseEntity<AircraftDto> create(@Valid @RequestBody AircraftDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(aircraftService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update aircraft")
    public ResponseEntity<AircraftDto> update(@PathVariable UUID id, @Valid @RequestBody AircraftDto dto) {
        return ResponseEntity.ok(aircraftService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete aircraft")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        aircraftService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
