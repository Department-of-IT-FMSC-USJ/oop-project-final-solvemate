package com.solvemate.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solvemate.dto.CompatibilityResultResponse;
import com.solvemate.exception.BadRequestException;
import com.solvemate.model.CompatibilityResult;
import com.solvemate.model.Polymer;
import com.solvemate.model.Solvent;
import com.solvemate.repository.CompatibilityResultRepository;
import com.solvemate.repository.PolymerRepository;
import com.solvemate.repository.SolventRepository;
import com.solvemate.service.CompatibilityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class CompatibilityServiceImpl implements CompatibilityService {

    private static final int    TOP_K          = 5;
    private static final String ML_SERVICE_URL = "http://localhost:5000/predict";

    private final PolymerRepository             polymerRepository;
    private final SolventRepository             solventRepository;
    private final CompatibilityResultRepository resultRepository;
    private final HttpClient                    httpClient;
    private final ObjectMapper                  objectMapper;

    public CompatibilityServiceImpl(PolymerRepository polymerRepository,
                                    SolventRepository solventRepository,
                                    CompatibilityResultRepository resultRepository) {
        this.polymerRepository = polymerRepository;
        this.solventRepository = solventRepository;
        this.resultRepository  = resultRepository;
        this.httpClient        = HttpClient.newHttpClient();
        this.objectMapper      = new ObjectMapper();
    }

    @Override
    public List<CompatibilityResultResponse> recommendTop5(Long polymerId) {

        Polymer polymer = polymerRepository.findById(polymerId)
                .orElseThrow(() -> new BadRequestException("Polymer not found: " + polymerId));

        List<Solvent> solvents = solventRepository.findAll();
        if (solvents.isEmpty()) {
            throw new BadRequestException("No solvents found in the database.");
        }

        List<CompatibilityResult> allResults = new ArrayList<>();
        for (Solvent solvent : solvents) {
            allResults.add(scoreWithMlModel(polymer, solvent));
        }

        List<CompatibilityResult> top5 = allResults.stream()
                .sorted(Comparator.comparingDouble(CompatibilityResult::getMlProbability).reversed())
                .limit(TOP_K)
                .toList();

        resultRepository.deleteByPolymer_PolymerId(polymerId);

        List<CompatibilityResult> saved = new ArrayList<>();
        for (int i = 0; i < top5.size(); i++) {
            CompatibilityResult r = top5.get(i);
            r.setRankPosition(i + 1);
            saved.add(resultRepository.save(r));
        }

        return saved.stream().map(this::toResponse).toList();
    }

    @Override
    public List<CompatibilityResultResponse> getResultsForPolymer(Long polymerId) {
        return resultRepository
                .findByPolymer_PolymerIdOrderByCompatibilityScoreDesc(polymerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private double computeRa(Polymer polymer, Solvent solvent) {
        double dD = solvent.getDeltaD() - polymer.getDeltaD();
        double dP = solvent.getDeltaP() - polymer.getDeltaP();
        double dH = solvent.getDeltaH() - polymer.getDeltaH();
        return Math.sqrt(4 * dD * dD + dP * dP + dH * dH);
    }

    private double callMlService(Polymer polymer, Solvent solvent) {
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("delta_d_solvent",      solvent.getDeltaD());
            body.put("delta_p_solvent",      solvent.getDeltaP());
            body.put("delta_h_solvent",      solvent.getDeltaH());
            body.put("molar_volume_cm3_mol", solvent.getMolarVolume());
            body.put("delta_d_polymer",      polymer.getDeltaD());
            body.put("delta_p_polymer",      polymer.getDeltaP());
            body.put("delta_h_polymer",      polymer.getDeltaH());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ML_SERVICE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode json = objectMapper.readTree(response.body());
                return json.get("probability").asDouble();
            }

            System.err.printf("[ML] Non-200 from ML service for solvent '%s': HTTP %d%n",
                    solvent.getName(), response.statusCode());

        } catch (Exception e) {
            System.err.printf("[ML] ML service unreachable for solvent '%s': %s%n",
                    solvent.getName(), e.getMessage());
        }

        return 0.0;
    }

    private CompatibilityResult scoreWithMlModel(Polymer polymer, Solvent solvent) {

        double dD = solvent.getDeltaD() - polymer.getDeltaD();
        double dP = solvent.getDeltaP() - polymer.getDeltaP();
        double dH = solvent.getDeltaH() - polymer.getDeltaH();

        double ra  = computeRa(polymer, solvent);
        double red = polymer.getR0() > 0 ? ra / polymer.getR0() : Double.MAX_VALUE;

        double mlProbability = callMlService(polymer, solvent);

        return new CompatibilityResult(
                polymer, solvent,
                round(dD), round(dP), round(dH),
                round(ra), round(red),
                round(mlProbability),
                round(mlProbability)
        );
    }

    private CompatibilityResultResponse toResponse(CompatibilityResult r) {
        CompatibilityResultResponse dto = new CompatibilityResultResponse();
        dto.setResultId(r.getResultId());
        dto.setPolymerId(r.getPolymer().getPolymerId());
        dto.setPolymerName(r.getPolymer().getPolymerName());
        dto.setSolventId(r.getSolvent().getId());
        dto.setSolventName(r.getSolvent().getName());
        dto.setDeltaDDifference(r.getDeltaDDifference());
        dto.setDeltaPDifference(r.getDeltaPDifference());
        dto.setDeltaHDifference(r.getDeltaHDifference());
        dto.setRaValue(r.getRaValue());
        dto.setRedValue(r.getRedValue());
        dto.setMlProbability(r.getMlProbability());
        dto.setCompatibilityScore(r.getCompatibilityScore());
        dto.setRankPosition(r.getRankPosition());
        dto.setResult(r.getResult());
        return dto;
    }

    private double round(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}