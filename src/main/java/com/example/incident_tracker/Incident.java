package com.example.incident_tracker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.Instant;

@Entity
@Table (name="incidents")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String description;

    @Column(name = "reported_by", nullable = false)
    private String reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(nullable = false, insertable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    // Getters and Setters
    public Long getId(){
        return id;
    }

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

    public Instant getTimestamp(){
        return timestamp;
    }
    public Status getStatus(){
        return status;
    }

    public void setStatus(Status status){
        this.status = status;
    }


}



