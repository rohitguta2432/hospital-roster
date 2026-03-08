package com.roster.backend.controller;

import com.roster.backend.dto.StaffRequest;
import com.roster.backend.model.Staff;
import com.roster.backend.model.StaffRole;
import com.roster.backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    public ResponseEntity<Staff> createStaff(@RequestBody StaffRequest request) {
        Staff staff = Staff.builder()
                .name(request.getName())
                .email(request.getEmail())
                .role(request.getRole())
                .build();
        return ResponseEntity.ok(staffService.createStaff(staff));
    }

    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<Staff>> getByRole(@PathVariable StaffRole role) {
        return ResponseEntity.ok(staffService.getStaffByRole(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Staff> getById(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getStaffById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }
}
