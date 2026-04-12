package com.solvemate.repository;

import com.solvemate.model.Solvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolventRepository extends JpaRepository<Solvent, Long> {
    Solvent findByName(String name);
}
