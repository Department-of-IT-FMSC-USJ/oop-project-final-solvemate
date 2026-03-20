package com.solvmate.controller;

import com.solvmate.model.Report;
import com.solvmate.service.ReportService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @PostMapping
    public Report createReport(@RequestBody Report report) {
        return service.generateReport(report);
    }

    @GetMapping
    public List<Report> getAllReports() {
        return service.getAllReports();
    }

    @GetMapping("/{id}")
    public Report getReportById(@PathVariable Long id) {
        return service.getReportById(id);
    }

    // PDF Export Endpoint
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportReportAsPdf(@PathVariable Long id) {
        byte[] pdfBytes = service.exportReportAsPdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}

