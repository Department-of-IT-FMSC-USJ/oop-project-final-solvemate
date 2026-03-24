package com.solvemate.dto;

public class CompatibilityResultResponse {

    private Long resultId;
    private Long polymerId;
    private String polymerName;
    private Long solventId;
    private String solventName;
    private double deltaDDifference;
    private double deltaPDifference;
    private double deltaHDifference;
    private double raValue;
    private double redValue;
    private double mlProbability;
    private double compatibilityScore;
    private int rankPosition;
    private String result;

    public CompatibilityResultResponse() {}

    // ── Getters / Setters ──────────────────────────────────────────────────────

    public Long getResultId()              { return resultId; }
    public Long getPolymerId()             { return polymerId; }
    public String getPolymerName()         { return polymerName; }
    public Long getSolventId()             { return solventId; }
    public String getSolventName()         { return solventName; }
    public double getDeltaDDifference()    { return deltaDDifference; }
    public double getDeltaPDifference()    { return deltaPDifference; }
    public double getDeltaHDifference()    { return deltaHDifference; }
    public double getRaValue()             { return raValue; }
    public double getRedValue()            { return redValue; }
    public double getMlProbability()       { return mlProbability; }
    public double getCompatibilityScore()  { return compatibilityScore; }
    public int getRankPosition()           { return rankPosition; }
    public String getResult()              { return result; }

    public void setResultId(Long resultId)                     { this.resultId = resultId; }
    public void setPolymerId(Long polymerId)                   { this.polymerId = polymerId; }
    public void setPolymerName(String polymerName)             { this.polymerName = polymerName; }
    public void setSolventId(Long solventId)                   { this.solventId = solventId; }
    public void setSolventName(String solventName)             { this.solventName = solventName; }
    public void setDeltaDDifference(double deltaDDifference)   { this.deltaDDifference = deltaDDifference; }
    public void setDeltaPDifference(double deltaPDifference)   { this.deltaPDifference = deltaPDifference; }
    public void setDeltaHDifference(double deltaHDifference)   { this.deltaHDifference = deltaHDifference; }
    public void setRaValue(double raValue)                     { this.raValue = raValue; }
    public void setRedValue(double redValue)                   { this.redValue = redValue; }
    public void setMlProbability(double mlProbability)         { this.mlProbability = mlProbability; }
    public void setCompatibilityScore(double compatibilityScore){ this.compatibilityScore = compatibilityScore; }
    public void setRankPosition(int rankPosition)              { this.rankPosition = rankPosition; }
    public void setResult(String result)                       { this.result = result; }
}
