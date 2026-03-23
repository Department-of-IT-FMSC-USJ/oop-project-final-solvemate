package com.solvemate.service;

import com.solvemate.dto.ApiResponse;
import com.solvemate.dto.PolymerRequest;
import com.solvemate.dto.PolymerResponse;

import java.util.List;

/**
 * PolymerService - interface defining the Polymer Management contract.
 *
 * OOP Concept - Abstraction:
 *   The interface hides implementation details and exposes only
 *   the operations the rest of the system needs.
 */
public interface PolymerService {

    PolymerResponse addPolymer(PolymerRequest request);

    PolymerResponse updatePolymer(Long id, PolymerRequest request);

    ApiResponse deletePolymer(Long id);

    PolymerResponse getPolymerById(Long id);

    List<PolymerResponse> getAllPolymers();

    List<PolymerResponse> getPolymersByCategory(String category);
}
