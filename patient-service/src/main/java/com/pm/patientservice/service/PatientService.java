package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;

    public List<PatientResponseDTO> getAllPatients() {
        log.debug("Fetching all patients from the database");
        return patientRepository.findAll().stream()
                .map(PatientMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        log.debug("Attempting to create a new patient with email: {}", patientRequestDTO.getEmail());
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            log.warn("Email already exists: {}", patientRequestDTO.getEmail());
            throw new EmailAlreadyExistsException("Patient with email " + patientRequestDTO.getEmail() + " already exists.");
        }

        Patient patient = PatientMapper.convertToModel(patientRequestDTO);
        Patient savedPatient = patientRepository.save(patient);
        log.debug("Patient successfully saved with ID: {}", savedPatient.getId());
        return PatientMapper.convertToDTO(savedPatient);

    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        log.debug("Attempting to update patient with ID: {}", id);
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Patient update failed: ID {} not found", id);
                    return new PatientNotFoundException("Patient not found with id: " + id);
                });

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            log.warn("Update failed: Email {} already exists for another patient", patientRequestDTO.getEmail());
            throw new EmailAlreadyExistsException("Patient with email " + patientRequestDTO.getEmail() + " already exists.");
        }

        existingPatient.setName(patientRequestDTO.getName());
        existingPatient.setEmail(patientRequestDTO.getEmail());
        existingPatient.setAddress(patientRequestDTO.getAddress());
        existingPatient.setDateOfBirth(patientRequestDTO.getDateOfBirth());

        Patient updatedPatient = patientRepository.save(existingPatient);
        log.debug("Patient with ID: {} successfully updated", id);
        return PatientMapper.convertToDTO(updatedPatient);
    }

    public void deletePatient(UUID id) {
        log.debug("Attempting to delete patient with ID: {}", id);
        if (!patientRepository.existsById(id)) {
            log.warn("Delete operation failed: Patient with ID {} not found", id);
            throw new PatientNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
        log.debug("Patient with ID: {} successfully deleted", id);
    }


}
