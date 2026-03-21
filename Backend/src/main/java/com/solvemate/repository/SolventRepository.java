package com.solvmate.repository;

import com.solvmate.model.Solvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolventRepository extends JpaRepository<Solvent, Long> {
    Solvent findByName(String name);
}
