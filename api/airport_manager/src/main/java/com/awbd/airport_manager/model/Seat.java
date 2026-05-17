package com.awbd.airport_manager.model;

import com.awbd.airport_manager.model.base.VersionedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "seats")
@Getter
@Setter
public class Seat extends VersionedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Seat number is mandatory")
    private String seatNo;

    private String type;
    private boolean isAvailable = true;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;
}
