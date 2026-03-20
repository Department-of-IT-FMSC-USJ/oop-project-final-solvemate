package com.solvmate.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "reports")
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String polymerName;
    private String solventName;
    private double compatibilityScore;
    private String trialResult; // success, partial, failed
    private String outcomeObservation;

    private double costAnalysis;
    private String envImpactSummary;
    private String euComplianceStatus;
    private String finalDecision;

    // Getters and Setters (or use Lombok @Data)
}