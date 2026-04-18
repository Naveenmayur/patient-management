package com.pm.patientservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class PatientResponseDTO {

    private UUID id;
    private String name;
    private String email;
    private String address;
    private LocalDate dateOfBirth;
    private LocalDate registrationDate;
}
