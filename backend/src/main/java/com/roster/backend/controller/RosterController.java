package com.roster.backend.controller;

import com.roster.backend.dto.RosterAssignmentResponse;
import com.roster.backend.dto.RosterGenerateRequest;
import com.roster.backend.model.RosterAssignment;
import com.roster.backend.service.RosterEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roster")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RosterController {

    private final RosterEngineService rosterEngineService;

    @PostMapping("/generate")
    public ResponseEntity<List<RosterAssignmentResponse>> generateRoster(
            @RequestBody RosterGenerateRequest request) {
        List<RosterAssignment> assignments = rosterEngineService.generateRoster(
                request.getStartDate(), request.getEndDate());
        return ResponseEntity.ok(toResponse(assignments));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<RosterAssignmentResponse>> getRosterByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(toResponse(rosterEngineService.getRosterByDate(date)));
    }

    @GetMapping("/range")
    public ResponseEntity<List<RosterAssignmentResponse>> getRosterByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(toResponse(rosterEngineService.getRosterByDateRange(start, end)));
    }

    private List<RosterAssignmentResponse> toResponse(List<RosterAssignment> assignments) {
        return assignments.stream()
                .map(a -> RosterAssignmentResponse.builder()
                        .id(a.getId())
                        .shiftDate(a.getShiftDate())
                        .shiftType(a.getShiftType())
                        .staffId(a.getStaff().getId())
                        .staffName(a.getStaff().getName())
                        .staffEmail(a.getStaff().getEmail())
                        .staffRole(a.getStaff().getRole())
                        .notified(a.isNotified())
                        .build())
                .collect(Collectors.toList());
    }
}
