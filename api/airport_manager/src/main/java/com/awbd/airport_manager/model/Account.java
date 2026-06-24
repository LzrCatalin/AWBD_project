package com.awbd.airport_manager.model;

import com.awbd.airport_manager.model.base.VersionedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account extends VersionedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Auth0 subject ("sub") claim — stable external identity used to link/provision accounts.
    @Column(name = "auth0_sub", unique = true)
    private String auth0Sub;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    // Null for Auth0-provisioned users (Auth0 owns their credentials).
    private String passwordHash;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    // Optional — Auth0-provisioned users may not have supplied a passport yet.
    @Pattern(regexp = "^[A-Z]{2}[0-9]{7}", message = "Passport format must be of type 'AB1234567'")
    @Column(unique = true)
    private String passportNo;

    @OneToMany(mappedBy = "account")
    private List<AccountRole> roles;
}
