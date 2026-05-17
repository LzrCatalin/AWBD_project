package com.awbd.airport_manager.model;

import com.awbd.airport_manager.model.base.VersionedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "flights")
@Getter
@Setter
public class Flight extends VersionedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "The flight no. is mandatory")
    @Column(unique = true)
    private String flightNo;

    @Future(message = "Departure time should be in the future")
    private LocalDateTime departureTime;

    @Future(message = "Arrival time should be in the future")
    private LocalDateTime arrivalTime;

    @NotBlank(message = "Departure city is mandatory")
    private String departureCity;

    @NotBlank(message = "Arrival city is mandatory")
    private String arrivalCity;

    @ManyToOne
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @ManyToOne
    @JoinColumn(name = "gate_id")
    private Gate gate;
}
