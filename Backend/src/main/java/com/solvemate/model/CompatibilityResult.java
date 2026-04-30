package com.solvemate.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * CompatibilityResult - stores calculated compatibility data between a Polymer and Solvent.
 *
 * OOP Concepts:
 *  - Encapsulation: private fields with getters/setters
 *  - Association: ManyToOne relationships to Polymer and Solvent (composition)
 *  - Abstraction: hides HSP math results behind a clean model
 */
@Entity
@Table(name = "compatibility_results")
public class CompatibilityResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    // Associations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polymer_id", nullable = false)
    private Polymer polymer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solvent_id", nullable = false)
    private Solvent solvent;

    // HSP difference components
    private double deltaDDifference;
    private double deltaPDifference;
    private double deltaHDifference;

    // Ra = sqrt(4*dD^2 + dP^2 + dH^2)
    private double raValue;

    // RED = Ra / R0   (< 1.0 = compatible, > 1.0 = not)
    private double redValue;

    // ML model probability (class 1 = compatible)
    private double mlProbability;

    // Final hybrid score (weighted combination)
    private double compatibilityScore;

    // Ranking position (1 = best)
    private int rankPosition;

    // Human-readable result
    private String result;    // COMPATIBLE / BORDERLINE / INCOMPATIBLE

    private LocalDateTime calculatedAt;

    // ── Constructors ──────────────────────────────────────────────────────────

    public CompatibilityResult() {
        this.calculatedAt = LocalDateTime.now();
    }

    public CompatibilityResult(Polymer polymer, Solvent solvent,
                                double deltaDDiff, double deltaPDiff, double deltaHDiff,
                                double ra, double red, double mlProb, double score) {
        this();
        this.polymer            = polymer;
        this.solvent            = solvent;
        this.deltaDDifference   = deltaDDiff;
        this.deltaPDifference   = deltaPDiff;
        this.deltaHDifference   = deltaHDiff;
        this.raValue            = ra;
        this.redValue           = red;
        this.mlProbability      = mlProb;
        this.compatibilityScore = score;
        this.result             = determineResult(red);
    }

    // ── Business logic ────────────────────────────────────────────────────────

    /**
     * Determines human-readable result based on RED value.
     * RED < 1.0  → COMPATIBLE
     * RED < 1.5  → BORDERLINE
     * RED >= 1.5 → INCOMPATIBLE
     */
    private String determineResult(double red) {
        if (red < 1.0)  return "COMPATIBLE";
        if (red < 1.5)  return "BORDERLINE";
        return "INCOMPATIBLE";
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Long getResultId()            { return resultId; }
    public Polymer getPolymer()          { return polymer; }
    public Solvent getSolvent()          { return solvent; }
    public double getDeltaDDifference()  { return deltaDDifference; }
    public double getDeltaPDifference()  { return deltaPDifference; }
    public double getDeltaHDifference()  { return deltaHDifference; }
    public double getRaValue()           { return raValue; }
    public double getRedValue()          { return redValue; }
    public double getMlProbability()     { return mlProbability; }
    public double getCompatibilityScore(){ return compatibilityScore; }
    public int getRankPosition()         { return rankPosition; }
    public String getResult()            { return result; }
    public LocalDateTime getCalculatedAt(){ return calculatedAt; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setPolymer(Polymer polymer)                    { this.polymer = polymer; }
    public void setSolvent(Solvent solvent)                    { this.solvent = solvent; }
    public void setDeltaDDifference(double v)                  { this.deltaDDifference = v; }
    public void setDeltaPDifference(double v)                  { this.deltaPDifference = v; }
    public void setDeltaHDifference(double v)                  { this.deltaHDifference = v; }
    public void setRaValue(double raValue)                     { this.raValue = raValue; }
    public void setRedValue(double redValue)                   { this.redValue = redValue; }
    public void setMlProbability(double mlProbability)         { this.mlProbability = mlProbability; }
    public void setCompatibilityScore(double compatibilityScore){ this.compatibilityScore = compatibilityScore; }
    public void setRankPosition(int rankPosition)              { this.rankPosition = rankPosition; }
    public void setResult(String result)                       { this.result = result; }
}
