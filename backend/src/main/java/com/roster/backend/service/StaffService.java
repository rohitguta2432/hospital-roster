package com.roster.backend.service;

import com.roster.backend.model.Staff;
import com.roster.backend.model.StaffRole;
import com.roster.backend.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;

    public Staff createStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public List<Staff> getStaffByRole(StaffRole role) {
        return staffRepository.findByRole(role);
    }

    public Staff getStaffById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id));
    }

    public void deleteStaff(Long id) {
        staffRepository.deleteById(id);
    }
}
