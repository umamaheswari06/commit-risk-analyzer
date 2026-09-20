package com.commitrisk.controller;

import com.commitrisk.dto.AnalysisSummaryDto;
import com.commitrisk.dto.AnalyzeRequest;
import com.commitrisk.dto.AnalyzeResponse;
import com.commitrisk.dto.DashboardStatsDto;
import com.commitrisk.service.CommitAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommitAnalysisController {

    private final CommitAnalysisService service;

    public CommitAnalysisController(CommitAnalysisService service) {
        this.service = service;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalyzeResponse> analyze(@Valid @RequestBody AnalyzeRequest request) {
        AnalyzeResponse response = service.analyze(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/analyses")
    public ResponseEntity<List<AnalysisSummaryDto>> getHistory() {
        return ResponseEntity.ok(service.getHistory());
    }

    @GetMapping("/analyses/{id}")
    public ResponseEntity<AnalyzeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(service.getDashboardStats());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
