package com.solvmate.controller;

import com.solvmate.model.Solvent;
import com.solvmate.service.SolventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solvents")
public class SolventController {

    private final SolventService service;

    public SolventController(SolventService service) {
        this.service = service;
    }

    @PostMapping
    public Solvent addSolvent(@RequestBody Solvent solvent) {
        return service.addSolvent(solvent);
    }

    @PutMapping("/{id}")
    public Solvent updateSolvent(@PathVariable Long id, @RequestBody Solvent solvent) {
        return service.updateSolvent(id, solvent);
    }

    @DeleteMapping("/{id}")
    public void deleteSolvent(@PathVariable Long id) {
        service.deleteSolvent(id);
    }

    @GetMapping
    public List<Solvent> getAllSolvents() {
        return service.getAllSolvents();
    }

    @GetMapping("/{name}")
    public Solvent getSolventByName(@PathVariable String name) {
        return service.getSolventByName(name);
    }
}
