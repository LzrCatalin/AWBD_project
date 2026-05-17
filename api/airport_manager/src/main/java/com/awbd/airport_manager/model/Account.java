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

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String passwordHash;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "A passport is mandatory")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{7}", message = "Passport format must be of type 'AB1234567'")
    @Column(unique = true)
    private String passportNo;

    @OneToMany(mappedBy = "account")
    private List<AccountRole> roles;
}
