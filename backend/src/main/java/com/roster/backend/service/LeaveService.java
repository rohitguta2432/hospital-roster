package com.roster.backend.service;

import com.roster.backend.model.LeaveRequest;
import com.roster.backend.model.Staff;
import com.roster.backend.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final StaffService staffService;
    private final RosterEngineService rosterEngineService;
    private final NotificationService notificationService;

    public LeaveRequest createLeave(Long staffId, LocalDate leaveDate, boolean isEmergency) {
        Staff staff = staffService.getStaffById(staffId);

        LeaveRequest leave = LeaveRequest.builder()
                .staff(staff)
                .leaveDate(leaveDate)
                .emergency(isEmergency)
                .build();

        LeaveRequest saved = leaveRequestRepository.save(leave);

        if (isEmergency) {
            log.warn("🚨 Emergency leave triggered for {} on {}", staff.getName(), leaveDate);
            rosterEngineService.handleEmergencyReschedule(staff, leaveDate);
        }

        return saved;
    }

    public List<LeaveRequest> getLeavesByDate(LocalDate date) {
        return leaveRequestRepository.findByLeaveDate(date);
    }

    public List<LeaveRequest> getLeavesByStaff(Long staffId) {
        return leaveRequestRepository.findByStaffId(staffId);
    }
}
