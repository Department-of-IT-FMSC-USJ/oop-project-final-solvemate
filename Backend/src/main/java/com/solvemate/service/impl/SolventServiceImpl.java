package com.solvemate.service.impl;

import com.solvemate.dto.ApiResponse;
import com.solvemate.dto.SolventRequest;
import com.solvemate.dto.SolventResponse;
import com.solvemate.exception.BadRequestException;
import com.solvemate.model.Solvent;
import com.solvemate.repository.SolventRepository;
import com.solvemate.service.SolventService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SolventServiceImpl implements SolventService {

    private final SolventRepository solventRepository;

    public SolventServiceImpl(SolventRepository solventRepository) {
        this.solventRepository = solventRepository;
    }



    @Override
    public SolventResponse addSolvent(SolventRequest request) {
        if (solventRepository.existsByName(request.getName())) {
            throw new BadRequestException("Solvent '" + request.getName() + "' already exists");
        }

        Solvent solvent = buildSolvent(new Solvent(), request);
        solventRepository.save(solvent);
        return mapToResponse(solvent);
    }



    @Override
    public SolventResponse updateSolvent(Long id, SolventRequest request) {
        Solvent solvent = solventRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Solvent not found with id: " + id));

        buildSolvent(solvent, request);
        solventRepository.save(solvent);
        return mapToResponse(solvent);
    }


    @Override
    public ApiResponse deleteSolvent(Long id) {
        if (!solventRepository.existsById(id)) {
            throw new BadRequestException("Solvent not found with id: " + id);
        }
        solventRepository.deleteById(id);
        return new ApiResponse("Solvent deleted successfully");
    }



    @Override
    public SolventResponse getSolventById(Long id) {
        Solvent solvent = solventRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Solvent not found with id: " + id));
        return mapToResponse(solvent);
    }

    @Override
    public List<SolventResponse> getAllSolvents() {
        return solventRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SolventResponse> getSolventsByEuStatus(boolean banned) {
        return solventRepository.findByEuBanStatus(banned)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SolventResponse> getSolventsByEnvScore(String score) {
        return solventRepository.findByEnvImpactScore(score)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    private Solvent buildSolvent(Solvent solvent, SolventRequest request) {
        solvent.setName(request.getName());
        solvent.setChemicalFormula(request.getChemicalFormula());
        solvent.setDeltaD(request.getDeltaD() != null ? request.getDeltaD() : 0.0);
        solvent.setDeltaP(request.getDeltaP() != null ? request.getDeltaP() : 0.0);
        solvent.setDeltaH(request.getDeltaH() != null ? request.getDeltaH() : 0.0);
        solvent.setMolarVolume(request.getMolarVolume() != null ? request.getMolarVolume() : 0.0);
        solvent.setCostPerLiter(request.getCostPerLiter() != null ? request.getCostPerLiter() : 0.0);
        solvent.setEnvImpactScore(request.getEnvImpactScore());
        solvent.setToxicityLevel(request.getToxicityLevel() != null ? request.getToxicityLevel() : 0.0);
        solvent.setEuBanStatus(request.getEuBanStatus() != null && request.getEuBanStatus());
        return solvent;
    }

    private SolventResponse mapToResponse(Solvent s) {
        SolventResponse resp = new SolventResponse();
        resp.setSolventId(s.getSolventId());
        resp.setName(s.getName());
        resp.setChemicalFormula(s.getChemicalFormula());
        resp.setDeltaD(s.getDeltaD());
        resp.setDeltaP(s.getDeltaP());
        resp.setDeltaH(s.getDeltaH());
        resp.setMolarVolume(s.getMolarVolume());
        resp.setDeltaT(s.getDeltaT());
        resp.setCostPerLiter(s.getCostPerLiter());
        resp.setEnvImpactScore(s.getEnvImpactScore());
        resp.setToxicityLevel(s.getToxicityLevel());
        resp.setEuBanStatus(s.isEuBanStatus());
        return resp;
    }
}
