package com.roster.backend.controller;

import com.roster.backend.dto.LeaveRequestDTO;
import com.roster.backend.model.LeaveRequest;
import com.roster.backend.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    public ResponseEntity<LeaveRequest> createLeave(@RequestBody LeaveRequestDTO request) {
        LeaveRequest leave = leaveService.createLeave(
                request.getStaffId(),
                request.getLeaveDate(),
                request.isEmergency());
        return ResponseEntity.ok(leave);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<LeaveRequest>> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(leaveService.getLeavesByDate(date));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<LeaveRequest>> getByStaff(@PathVariable Long staffId) {
        return ResponseEntity.ok(leaveService.getLeavesByStaff(staffId));
    }
}
