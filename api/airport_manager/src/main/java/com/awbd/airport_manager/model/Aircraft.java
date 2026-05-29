package com.awbd.airport_manager.model;

import com.awbd.airport_manager.model.base.VersionedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "aircrafts")
@Getter
@Setter
public class Aircraft extends VersionedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Model is mandatory")
    private String model;

    @Min(value = 1, message = "Capacity needs to be at least 1")
    private int capacity;

    private String planeNo;
}
