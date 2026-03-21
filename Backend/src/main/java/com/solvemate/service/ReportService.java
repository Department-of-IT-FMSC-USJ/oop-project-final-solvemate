package com.solvmate.service;

import com.solvmate.model.Report;
import com.solvmate.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.io.ByteArrayOutputStream;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@Service
public class ReportService {

    private final ReportRepository repository;

    public ReportService(ReportRepository repository) {
        this.repository = repository;
    }

    public Report generateReport(Report report) {
        return repository.save(report);
    }

    public List<Report> getAllReports() {
        return repository.findAll();
    }

    public Report getReportById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Report not found"));
    }

    // PDF Export
    public byte[] exportReportAsPdf(Long id) {
        Report report = getReportById(id);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("SolvMate Report"));
        document.add(new Paragraph("Polymer: " + report.getPolymerName()));
        document.add(new Paragraph("Solvent: " + report.getSolventName()));
        document.add(new Paragraph("Compatibility Score: " + report.getCompatibilityScore()));
        document.add(new Paragraph("Trial Result: " + report.getTrialResult()));
        document.add(new Paragraph("Observation: " + report.getOutcomeObservation()));
        document.add(new Paragraph("Cost Analysis: $" + report.getCostAnalysis()));
        document.add(new Paragraph("Environmental Impact: " + report.getEnvImpactSummary()));
        document.add(new Paragraph("EU Compliance: " + report.getEuComplianceStatus()));
        document.add(new Paragraph("Final Decision: " + report.getFinalDecision()));

        document.close();
        return baos.toByteArray();
    }
}
