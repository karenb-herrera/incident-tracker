package com.example.incident_tracker.service;

import com.example.incident_tracker.Incident;
import com.example.incident_tracker.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import com.example.incident_tracker.Status;
import com.example.incident_tracker.Severity;
import com.example.incident_tracker.dto.IncidentCreateRequestDto;
import com.example.incident_tracker.dto.IncidentPatchRequestDto;

import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {
    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository){
        this.incidentRepository = incidentRepository;
    }

    // Create an Incident
    public Incident createIncident(IncidentCreateRequestDto dto){
        Incident incident = new Incident();
        incident.setTitle(dto.getTitle());
        incident.setDescription(dto.getDescription());
        incident.setReportedBy(dto.getReportedBy());
        incident.setSeverity(dto.getSeverity());

        // Default value when creating an incident
        incident.setStatus(Status.OPEN);
        return incidentRepository.save(incident);
    }  

    // Get all Incidents
    public List<Incident> getIncidents(Status status, Severity severity){
        String statusValue = status == null ? null : status.name();
        String severityValue = severity == null ? null : severity.name();

        return incidentRepository.findAllByFilters(statusValue, severityValue);
    }

    // Get one Incident by ID
    public Optional<Incident> getIncident(Long id){
        return incidentRepository.findById(id);
    }


    // Patch the Status
    public Optional<Incident> patchIncident(Long id, IncidentPatchRequestDto dto){ 
        Optional<Incident> existingIncident = incidentRepository.findById(id);

        if(!existingIncident.isPresent()){
            return Optional.empty();
        }

        Incident incident = existingIncident.get();
        incident.setStatus(dto.getStatus());

        Incident patchedIncident = incidentRepository.save(incident);
        return Optional.of(patchedIncident);
    }

    // Delete one Incident by ID
    public boolean deleteIncident(Long id){
        // ID doesnt exist
        if (!incidentRepository.existsById(id)) {
            return false;
        }

        // ID exists, delete it
        incidentRepository.deleteById(id);
        return true;
    }
}
