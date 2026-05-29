package com.awbd.airport_manager.controller;

import com.awbd.airport_manager.dto.FlightDto;
import com.awbd.airport_manager.service.api.FlightService;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.awbd.airport_manager.util.enums.ApiPaths.Flights;

@RestController
@RequestMapping(Flights)
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping("/search")
    @Operation(summary = "Search flights with filters, sorters and pagination")
    public ResponseEntity<PagedResponse<FlightDto>> search(@RequestBody SearchDTO searchDTO) {
        return ResponseEntity.ok(flightService.search(searchDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flight by id")
    public ResponseEntity<FlightDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(flightService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create flight")
    public ResponseEntity<FlightDto> create(@Valid @RequestBody FlightDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flightService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update flight")
    public ResponseEntity<FlightDto> update(@PathVariable UUID id, @Valid @RequestBody FlightDto dto) {
        return ResponseEntity.ok(flightService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete flight")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        flightService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
