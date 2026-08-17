package com.example.incident_tracker.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.incident_tracker.Incident;
import com.example.incident_tracker.Status;
import com.example.incident_tracker.Severity;
import com.example.incident_tracker.dto.IncidentCreateRequestDto;
import com.example.incident_tracker.dto.IncidentPatchRequestDto;
import com.example.incident_tracker.service.IncidentService;

import jakarta.validation.Valid;



@RestController
public class IncidentController {
    
    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService){
        this.incidentService = incidentService;
    }

    @PostMapping("/incidents")
    public ResponseEntity<Incident> createIncident(@Valid @RequestBody IncidentCreateRequestDto dto) {
        Incident createdIncident = incidentService.createIncident(dto);
        // Status Code 201 - Created
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIncident);
    }

    @GetMapping("/incidents")
    public ResponseEntity<List<Incident>> getIncidents(@RequestParam(required = false) Status status, 
        @RequestParam(required = false) Severity severity) {

        return ResponseEntity.ok(incidentService.getIncidents(status, severity));
    }

    @GetMapping("/incidents/{id}")
    public ResponseEntity<Incident> getIncident(@PathVariable Long id) {
        Optional<Incident> incident = incidentService.getIncident(id);
        
        if(!incident.isPresent()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(incident.get());
    }
    
    @PatchMapping("/incidents/{id}")
    public ResponseEntity<Incident> patchIncident(@PathVariable Long id, @Valid @RequestBody IncidentPatchRequestDto dto){
        Optional<Incident> updatedIncident = incidentService.patchIncident(id, dto);

        if(!updatedIncident.isPresent()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedIncident.get());
    }
    
    @DeleteMapping("/incidents/{id}")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        boolean isDeleted = incidentService.deleteIncident(id);

        // ID not found, Status Code 404 Not Found
        if(!isDeleted){
            return ResponseEntity.notFound().build();
        }

        // It deleted, Status Code 204 No content
        return ResponseEntity.noContent().build();

    }

}
