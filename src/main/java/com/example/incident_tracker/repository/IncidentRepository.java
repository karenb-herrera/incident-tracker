package com.example.incident_tracker.repository;

import com.example.incident_tracker.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long>{  
    
    @Query(value = """
            SELECT * 
            FROM incidents
            WHERE (:status is NULL OR status = :status) AND (:severity is NULL OR severity = :severity)
            """, nativeQuery = true)
    List<Incident> findAllByFilters(@Param("status") String status, @Param("severity") String severity);
}
