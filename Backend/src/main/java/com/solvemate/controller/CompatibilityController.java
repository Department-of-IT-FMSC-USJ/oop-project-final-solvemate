package com.solvemate.controller;

import com.solvemate.dto.CompatibilityResultResponse;
import com.solvemate.service.CompatibilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compatibility")
@CrossOrigin(origins = "*")
public class CompatibilityController {

    private final CompatibilityService compatibilityService;

    public CompatibilityController(CompatibilityService compatibilityService) {
        this.compatibilityService = compatibilityService;
    }

    @PostMapping("/recommend/{polymerId}")
    public ResponseEntity<List<CompatibilityResultResponse>> recommend(
            @PathVariable Long polymerId) {
        return ResponseEntity.ok(compatibilityService.recommendTop5(polymerId));
    }

    @GetMapping("/results/{polymerId}")
    public ResponseEntity<List<CompatibilityResultResponse>> getResults(
            @PathVariable Long polymerId) {
        return ResponseEntity.ok(compatibilityService.getResultsForPolymer(polymerId));
    }
}
