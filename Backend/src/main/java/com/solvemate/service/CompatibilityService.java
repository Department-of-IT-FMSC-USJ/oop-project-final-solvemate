package com.solvemate.service;

import com.solvemate.dto.CompatibilityResultResponse;
import java.util.List;

public interface CompatibilityService {
    List<CompatibilityResultResponse> recommendTop5(Long polymerId);
    List<CompatibilityResultResponse> getResultsForPolymer(Long polymerId);
}