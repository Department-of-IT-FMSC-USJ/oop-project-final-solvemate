package com.solvmate.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "solvents")
@Data   // Lombok generates getters, setters, toString, equals, hashCode
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
