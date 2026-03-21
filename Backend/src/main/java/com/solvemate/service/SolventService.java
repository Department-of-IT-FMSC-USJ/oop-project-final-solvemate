package com.solvmate.service;

import com.solvmate.model.Solvent;
import com.solvmate.repository.SolventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolventService {

    private final SolventRepository repository;

    public SolventService(SolventRepository repository) {
        this.repository = repository;
    }

    public Solvent addSolvent(Solvent solvent) {
        return repository.save(solvent);
    }

    public Solvent updateSolvent(Long id, Solvent updatedSolvent) {
        return repository.findById(id).map(solvent -> {
            solvent.setName(updatedSolvent.getName());
            solvent.setDeltaD(updatedSolvent.getDeltaD());
            solvent.setDeltaP(updatedSolvent.getDeltaP());
            solvent.setDeltaH(updatedSolvent.getDeltaH());
            solvent.setMolarVolume(updatedSolvent.getMolarVolume());
            solvent.setDeltaT(updatedSolvent.getDeltaT());
            solvent.setCostPerLiter(updatedSolvent.getCostPerLiter());
            solvent.setEnvImpactScore(updatedSolvent.getEnvImpactScore());
            solvent.setEuBanStatus(updatedSolvent.isEuBanStatus());
            return repository.save(solvent);
        }).orElseThrow(() -> new RuntimeException("Solvent not found"));
    }

    public void deleteSolvent(Long id) {
        repository.deleteById(id);
    }

    public List<Solvent> getAllSolvents() {
        return repository.findAll();
    }

    public Solvent getSolventByName(String name) {
        return repository.findByName(name);
    }
}

