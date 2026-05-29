package com.awbd.airport_manager.model;

import com.awbd.airport_manager.model.base.VersionedEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "account_roles")
@Getter
@Setter
public class AccountRole extends VersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
