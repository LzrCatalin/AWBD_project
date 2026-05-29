package com.awbd.airport_manager.model;

import com.awbd.airport_manager.model.base.VersionedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "gates")
@Getter
@Setter
public class Gate extends VersionedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Gate number is mandatory")
    @Column(unique = true)
    private String gateNo;

    private String terminal;
}
