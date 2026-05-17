package com.awbd.airport_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AccountDto {

    private UUID id;

    @Email
    @NotBlank
    private String email;

    private String password;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "A passport is mandatory")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{7}", message = "Passport format must be of type 'AB1234567'")
    private String passportNo;

    private List<String> roles;
}
