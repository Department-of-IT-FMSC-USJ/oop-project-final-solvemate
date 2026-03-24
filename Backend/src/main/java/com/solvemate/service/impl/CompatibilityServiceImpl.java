package com.solvemate.service.impl;

import com.solvemate.dto.CompatibilityResultResponse;
import com.solvemate.exception.BadRequestException;
import com.solvemate.model.CompatibilityResult;
import com.solvemate.model.Polymer;
import com.solvemate.model.Recommendation;
import com.solvemate.model.Solvent;
import com.solvemate.repository.CompatibilityResultRepository;
import com.solvemate.repository.PolymerRepository;
import com.solvemate.repository.RecommendationRepository;
import com.solvemate.repository.SolventRepository;
import com.solvemate.service.CompatibilityService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

/**
 * CompatibilityServiceImpl - implements the hybrid Hansen + ML recommendation engine.
 *
 * OOP Concepts:
 *  - Polymorphism      : implements CompatibilityService interface
 *  - Encapsulation     : HSP math and ML call are private methods
 *  - Abstraction       : callers see only recommendTop5() / getResultsForPolymer()
 *  - Single Responsibility : only handles compatibility calculation & recommendation
 *
 * Algorithm (mirrors the Python recommend_top5.py):
 *  1. For each solvent, compute Ra = sqrt(4*dD^2 + dP^2 + dH^2)
 *  2. Compute RED = Ra / R0
 *  3. Call Python ML microservice to get P(compatible) for each solvent
 *  4. Filter: RED < 1.2  (fallback: all solvents if none pass)
 *  5. Sort by ML probability descending → take top 5
 *  6. Persist results + recommendation
 */
@Service
public class CompatibilityServiceImpl implements CompatibilityService {

    private static final double RED_THRESHOLD   = 1.2;
    private static final int    TOP_K           = 5;
    private static final String ML_SERVICE_URL  = "http://localhost:5000/predict";

    private final PolymerRepository             polymerRepository;
    private final SolventRepository             solventRepository;
    private final CompatibilityResultRepository resultRepository;
    private final RecommendationRepository      recommendationRepository;
    private final RestTemplate                  restTemplate;

    public CompatibilityServiceImpl(PolymerRepository polymerRepository,
                                    SolventRepository solventRepository,
                                    CompatibilityResultRepository resultRepository,
                                    RecommendationRepository recommendationRepository) {
        this.polymerRepository        = polymerRepository;
        this.solventRepository        = solventRepository;
        this.resultRepository         = resultRepository;
        this.recommendationRepository = recommendationRepository;
        this.restTemplate             = new RestTemplate();
    }

    // ── Public API ────────────────────────────────────────────────────────────

    @Override
    public List<CompatibilityResultResponse> recommendTop5(Long polymerId) {
        Polymer polymer = polymerRepository.findById(polymerId)
                .orElseThrow(() -> new BadRequestException("Polymer not found: " + polymerId));

        List<Solvent> solvents = solventRepository.findAll();
        if (solvents.isEmpty()) {
            throw new BadRequestException("No solvents found in the database");
        }

        // Step 1 & 2: Calculate Ra, RED and call ML for all solvents
        List<CompatibilityResult> allResults = new ArrayList<>();
        for (Solvent solvent : solvents) {
            CompatibilityResult result = calculateCompatibility(polymer, solvent);
            allResults.add(result);
        }

        // Step 3: Filter by RED threshold
        List<CompatibilityResult> filtered = allResults.stream()
                .filter(r -> r.getRedValue() < RED_THRESHOLD)
                .toList();

        // Step 4: Fallback if none pass RED threshold
        if (filtered.isEmpty()) {
            filtered = allResults;
        }

        // Step 5: Sort by ML probability descending → top K
        List<CompatibilityResult> top5 = filtered.stream()
                .sorted(Comparator.comparingDouble(CompatibilityResult::getMlProbability).reversed())
                .limit(TOP_K)
                .toList();

        // Step 6: Assign rank positions and persist
        List<CompatibilityResult> ranked = new ArrayList<>(top5);
        for (int i = 0; i < ranked.size(); i++) {
            ranked.get(i).setRankPosition(i + 1);
        }
        resultRepository.saveAll(ranked);

        // Save Recommendation record
        Recommendation recommendation = new Recommendation(polymer, ranked);
        recommendation.rankSolvents();
        recommendationRepository.save(recommendation);

        return ranked.stream().map(this::mapToResponse).toList();
    }

    @Override
    public List<CompatibilityResultResponse> getResultsForPolymer(Long polymerId) {
        return resultRepository
                .findByPolymer_PolymerIdOrderByCompatibilityScoreDesc(polymerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Private: HSP Calculation ──────────────────────────────────────────────

    /**
     * Calculates compatibility between a polymer and a single solvent.
     * Uses Hansen Ra formula + ML microservice probability.
     *
     * Ra  = sqrt( 4*(dD_s - dD_p)^2 + (dP_s - dP_p)^2 + (dH_s - dH_p)^2 )
     * RED = Ra / R0
     */
    private CompatibilityResult calculateCompatibility(Polymer polymer, Solvent solvent) {
        double dD = solvent.getDeltaD() - polymer.getDeltaD();
        double dP = solvent.getDeltaP() - polymer.getDeltaP();
        double dH = solvent.getDeltaH() - polymer.getDeltaH();

        double ra  = Math.sqrt(4 * dD * dD + dP * dP + dH * dH);
        double red = ra / polymer.getR0();

        // Call ML microservice
        double mlProb = callMlService(
                solvent.getDeltaD(), solvent.getDeltaP(), solvent.getDeltaH(),
                polymer.getDeltaD(), polymer.getDeltaP(), polymer.getDeltaH(),
                polymer.getR0()
        );

        // Hybrid score: 60% ML + 40% physics (inverted RED, capped at 1)
        double physicsScore = Math.max(0, 1.0 - (red / 2.0));
        double hybridScore  = Math.round((0.6 * mlProb + 0.4 * physicsScore) * 10000.0) / 100.0;

        CompatibilityResult result = new CompatibilityResult(
                polymer, solvent,
                round(dD), round(dP), round(dH),
                round(ra), round(red),
                round(mlProb), hybridScore
        );

        return result;
    }

    // ── Private: ML Service Call ──────────────────────────────────────────────

    /**
     * Calls the Python Flask ML microservice to get the probability
     * that the solvent is compatible with the polymer (class 1).
     *
     * Falls back to physics-only score if the ML service is unavailable.
     */
    private double callMlService(double dDs, double dPs, double dHs,
                                  double dDp, double dPp, double dHp,
                                  double r0) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("delta_d_solvent", dDs);
            payload.put("delta_p_solvent", dPs);
            payload.put("delta_h_solvent", dHs);
            payload.put("delta_d_polymer", dDp);
            payload.put("delta_p_polymer", dPp);
            payload.put("delta_h_polymer", dHp);
            payload.put("R0", r0);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(ML_SERVICE_URL, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Object prob = response.getBody().get("probability");
                return prob != null ? ((Number) prob).doubleValue() : 0.5;
            }
        } catch (Exception e) {
            // ML service unavailable — fall back to 0.5 neutral probability
            System.err.println("[CompatibilityService] ML service unavailable: " + e.getMessage());
        }
        return 0.5;
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private CompatibilityResultResponse mapToResponse(CompatibilityResult r) {
        CompatibilityResultResponse resp = new CompatibilityResultResponse();
        resp.setResultId(r.getResultId());
        resp.setPolymerId(r.getPolymer().getPolymerId());
        resp.setPolymerName(r.getPolymer().getPolymerName());
        resp.setSolventId(r.getSolvent().getSolventId());
        resp.setSolventName(r.getSolvent().getName());
        resp.setDeltaDDifference(r.getDeltaDDifference());
        resp.setDeltaPDifference(r.getDeltaPDifference());
        resp.setDeltaHDifference(r.getDeltaHDifference());
        resp.setRaValue(r.getRaValue());
        resp.setRedValue(r.getRedValue());
        resp.setMlProbability(r.getMlProbability());
        resp.setCompatibilityScore(r.getCompatibilityScore());
        resp.setRankPosition(r.getRankPosition());
        resp.setResult(r.getResult());
        return resp;
    }

    private double round(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}
