package com.emergency.system.controller;

import com.emergency.system.model.Emergency;
import com.emergency.system.repository.EmergencyRepository;
import com.emergency.system.service.EmergencySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Exports emergency data as CSV — admin only.
 *
 * GET /api/export/emergencies.csv  — full export
 * GET /api/export/emergencies.csv?riskLevel=HIGH  — filtered export
 */
@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class EmergencyExportController {

    private final EmergencyRepository emergencyRepository;
    private final EmergencySearchService searchService;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Download all (or filtered) emergencies as a UTF-8 CSV file.
     * Filename includes a timestamp for uniqueness.
     */
    @GetMapping("/emergencies.csv")
    public ResponseEntity<byte[]> exportEmergencies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Emergency.RiskLevel riskLevel,
            @RequestParam(required = false) Emergency.EmergencyStatus status) {

        List<Emergency> emergencies;

        boolean hasFilter = keyword != null || riskLevel != null || status != null;
        if (hasFilter) {
            emergencies = searchService.search(new EmergencySearchService.SearchParams(
                    keyword, riskLevel, status, null, null, null, null, null, null));
        } else {
            emergencies = emergencyRepository.findAll();
        }

        String csv = buildCsv(emergencies);
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        String filename = "emergencies_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                ".csv";

        log.info("Exporting {} emergencies as CSV: {}", emergencies.size(), filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                .header("X-Record-Count", String.valueOf(emergencies.size()))
                .body(bytes);
    }

    private String buildCsv(List<Emergency> list) {
        StringBuilder sb = new StringBuilder();
        // Header
        sb.append("id,message,riskLevel,status,latitude,longitude,")
          .append("fallDetected,movement,userResponse,aiAction,")
          .append("reportedByDeviceId,createdAt,resolvedAt\n");

        // Rows
        for (Emergency e : list) {
            sb.append(e.getId()).append(",")
              .append(csvCell(e.getMessage())).append(",")
              .append(e.getRiskLevel()).append(",")
              .append(e.getStatus()).append(",")
              .append(e.getLatitude() != null ? e.getLatitude() : "").append(",")
              .append(e.getLongitude() != null ? e.getLongitude() : "").append(",")
              .append(e.getFallDetected() != null ? e.getFallDetected() : "false").append(",")
              .append(csvCell(e.getMovement())).append(",")
              .append(csvCell(e.getUserResponse())).append(",")
              .append(csvCell(e.getAiAction())).append(",")
              .append(csvCell(e.getReportedByDeviceId())).append(",")
              .append(e.getCreatedAt() != null ? e.getCreatedAt().format(FMT) : "").append(",")
              .append(e.getResolvedAt() != null ? e.getResolvedAt().format(FMT) : "")
              .append("\n");
        }
        return sb.toString();
    }

    /** Wraps a value in quotes and escapes internal quotes. */
    private String csvCell(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
