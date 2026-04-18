package com.pm.patientservice.controller;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    // Get All patients
     // GET /api/patients
    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> findAll() {
        List<PatientResponseDTO> dtoList = patientService.getAllPatients();
        return ResponseEntity.ok(dtoList);
    }

    // Save Patient api
    @PostMapping
    public ResponseEntity<PatientResponseDTO> savePatient(
            @Valid @RequestBody PatientRequestDTO patientRequestDTO) {

        PatientResponseDTO savedPatient = patientService.createPatient(patientRequestDTO);
        return ResponseEntity.ok().body(savedPatient);
    }

}
