package com.awbd.airport_manager.controller;

import com.awbd.airport_manager.dto.GateDto;
import com.awbd.airport_manager.service.api.GateService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.awbd.airport_manager.util.enums.ApiPaths.Gates;

@RestController
@RequestMapping(Gates)
@RequiredArgsConstructor
public class GateController {

    private final GateService gateService;

    @GetMapping
    @Operation(summary = "Get all gates")
    public ResponseEntity<List<GateDto>> getAll() {
        return ResponseEntity.ok(gateService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get gate by id")
    public ResponseEntity<GateDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(gateService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create gate")
    public ResponseEntity<GateDto> create(@Valid @RequestBody GateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gateService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update gate")
    public ResponseEntity<GateDto> update(@PathVariable UUID id, @Valid @RequestBody GateDto dto) {
        return ResponseEntity.ok(gateService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete gate")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        gateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
