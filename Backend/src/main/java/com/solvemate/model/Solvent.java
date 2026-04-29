package com.solvemate.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "solvents")
@Data
public class Solvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double deltaD;
    private double deltaP;
    private double deltaH;
    private double molarVolume;
    private double deltaT;

    private double costPerLiter;
    private String envImpactScore;
    private boolean euBanStatus;
}
