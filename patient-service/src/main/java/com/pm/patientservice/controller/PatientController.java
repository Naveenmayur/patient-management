package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.dto.validators.CreatePatientValidationGroup;
import com.pm.patientservice.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "APIs for managing patients")
@Slf4j
public class PatientController {

    private final PatientService patientService;

    // Get All patients
     // GET /api/patients
    @GetMapping
    @Operation(summary = "Get all patients", description = "Retrieve a list of all patients")
    public ResponseEntity<List<PatientResponseDTO>> findAll() {
        log.info("Request to fetch all patients received");
        List<PatientResponseDTO> dtoList = patientService.getAllPatients();
        log.info("Returning {} patients", dtoList.size());
        return ResponseEntity.ok(dtoList);
    }

    // Save Patient api
    @PostMapping
    @Operation(summary = "Create a new patient", description = "Create a new patient with the provided details")
    public ResponseEntity<PatientResponseDTO> savePatient(
            @Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequestDTO) {

        log.info("Request to create a new patient with email: {}", patientRequestDTO.getEmail());
        PatientResponseDTO savedPatient = patientService.createPatient(patientRequestDTO);
        log.info("Patient created successfully with ID: {}", savedPatient.getId());
        return ResponseEntity.ok().body(savedPatient);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing patient", description = "Update the details of an existing patient by ID")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @PathVariable UUID id, @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO) {
        log.info("Request to update patient with ID: {}", id);
        PatientResponseDTO savedPatient = patientService.updatePatient(id, patientRequestDTO);
        log.info("Patient with ID: {} updated successfully", id);
        return ResponseEntity.ok().body(savedPatient);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient", description = "Delete an existing patient by ID")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        log.info("Request to delete patient with ID: {}", id);
        patientService.deletePatient(id);
        log.info("Patient with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

}
