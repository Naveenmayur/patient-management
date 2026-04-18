package com.pm.patientservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequestDTO {

    @NotNull(message = "Name cannot be null")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Address cannot be null")
    private String address;

    @NotNull(message = "Date of Birth cannot be null")
    private LocalDate dateOfBirth;

    @NotNull(message = "Registration Date cannot be null")
    private LocalDate registrationDate;
}
