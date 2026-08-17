package com.example.incident_tracker.dto;

import com.example.incident_tracker.Severity;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IncidentCreateRequestDto {

    @NotBlank
    private String title;

    private String description;

    @JsonProperty("reported_by")
    @NotBlank
    private String reportedBy;

    @NotNull
    private Severity severity;

    // Getters and Setters
    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getReportedBy(){
        return reportedBy;
    }

    public void setReportedBy(String reportedBy){
        this.reportedBy = reportedBy;
    }

    public Severity getSeverity(){
        return severity;
    }

    public void setSeverity(Severity severity){
        this.severity = severity;
    }
}
