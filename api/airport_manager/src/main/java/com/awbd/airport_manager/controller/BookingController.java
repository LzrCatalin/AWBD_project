package com.awbd.airport_manager.controller;

import com.awbd.airport_manager.dto.BookingDto;
import com.awbd.airport_manager.service.api.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.awbd.airport_manager.util.enums.ApiPaths.Booking;

@RestController
@RequestMapping(Booking)
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    @Operation(summary = "Get all bookings")
    public ResponseEntity<List<BookingDto>> getAll() {
        return ResponseEntity.ok(bookingService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by id")
    public ResponseEntity<BookingDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create booking")
    public ResponseEntity<BookingDto> create(@Valid @RequestBody BookingDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel booking")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
