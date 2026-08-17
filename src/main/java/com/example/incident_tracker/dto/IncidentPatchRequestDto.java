package com.example.incident_tracker.dto;

import com.example.incident_tracker.Status;
import jakarta.validation.constraints.NotNull;

public class IncidentPatchRequestDto {

    @NotNull
    private Status status;

    // Getter and Setter
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
}
