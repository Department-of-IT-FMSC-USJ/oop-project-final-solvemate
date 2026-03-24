package com.solvemate.controller;

import com.solvemate.dto.CompatibilityResultResponse;
import com.solvemate.service.CompatibilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CompatibilityController - REST controller for the Compatibility Calculation
 * and Solvent Recommendation Module.
 *
 * OOP Concept - Separation of Concerns:
 *   Controller only routes HTTP calls. All HSP math + ML logic is in the service.
 *
 * Endpoints:
 *   POST /api/compatibility/recommend/{polymerId}
 *       - Runs full hybrid analysis and returns Top-5 recommendations
 *
 *   GET  /api/compatibility/results/{polymerId}
 *       - Returns previously stored results for a polymer
 */
@RestController
@RequestMapping("/api/compatibility")
@CrossOrigin(origins = "*")
public class CompatibilityController {

    private final CompatibilityService compatibilityService;

    public CompatibilityController(CompatibilityService compatibilityService) {
        this.compatibilityService = compatibilityService;
    }

    /**
     * Runs the hybrid HSP + ML recommendation for a given polymer.
     * Returns the Top-5 compatible solvents ranked by ML probability.
     */
    @PostMapping("/recommend/{polymerId}")
    public ResponseEntity<List<CompatibilityResultResponse>> recommend(
            @PathVariable Long polymerId) {
        return ResponseEntity.ok(compatibilityService.recommendTop5(polymerId));
    }

    /**
     * Returns stored compatibility results for a polymer (from DB).
     */
    @GetMapping("/results/{polymerId}")
    public ResponseEntity<List<CompatibilityResultResponse>> getResults(
            @PathVariable Long polymerId) {
        return ResponseEntity.ok(compatibilityService.getResultsForPolymer(polymerId));
    }
}
