package com.roster.backend.service;

import com.roster.backend.model.*;
import com.roster.backend.repository.LeaveRequestRepository;
import com.roster.backend.repository.RosterAssignmentRepository;
import com.roster.backend.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RosterEngineService {

    private final StaffRepository staffRepository;
    private final RosterAssignmentRepository rosterAssignmentRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final NotificationService notificationService;

    // Minimum coverage per shift
    private static final Map<StaffRole, Integer> MIN_COVERAGE = Map.of(
            StaffRole.DOCTOR, 2,
            StaffRole.NURSE, 6,
            StaffRole.WARDEN, 10);

    /**
     * Generates the roster for a given date range.
     * For each day and each shift, assigns the minimum number of staff per role
     * while respecting leave exclusions and the Night-to-Morning rest constraint.
     */
    @Transactional
    public List<RosterAssignment> generateRoster(LocalDate startDate, LocalDate endDate) {
        List<RosterAssignment> allAssignments = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            for (ShiftType shiftType : ShiftType.values()) {
                List<RosterAssignment> shiftAssignments = assignShift(date, shiftType);
                allAssignments.addAll(shiftAssignments);
            }
        }

        log.info("✅ Roster generated for {} to {}: {} total assignments",
                startDate, endDate, allAssignments.size());
        return allAssignments;
    }

    /**
     * Assigns staff to a single shift on a single day, respecting all constraints.
     */
    private List<RosterAssignment> assignShift(LocalDate date, ShiftType shiftType) {
        List<RosterAssignment> assignments = new ArrayList<>();

        // Get staff already assigned to this shift (in case of partial generation)
        List<RosterAssignment> existing = rosterAssignmentRepository
                .findByShiftDateAndShiftType(date, shiftType);

        for (StaffRole role : StaffRole.values()) {
            int required = MIN_COVERAGE.get(role);

            // Count already assigned staff of this role
            long alreadyAssigned = existing.stream()
                    .filter(a -> a.getStaff().getRole() == role)
                    .count();

            int needed = (int) (required - alreadyAssigned);
            if (needed <= 0)
                continue;

            // Get available staff for this role
            List<Staff> available = getAvailableStaff(role, date, shiftType);

            // Exclude already assigned staff
            Set<Long> assignedIds = existing.stream()
                    .map(a -> a.getStaff().getId())
                    .collect(Collectors.toSet());
            available.removeIf(s -> assignedIds.contains(s.getId()));

            if (available.size() < needed) {
                log.warn("⚠️ Insufficient {} for {} shift on {}. Need {}, available {}.",
                        role, shiftType, date, needed, available.size());
            }

            // Shuffle for fairness and assign
            Collections.shuffle(available);
            int toAssign = Math.min(needed, available.size());

            for (int i = 0; i < toAssign; i++) {
                Staff staff = available.get(i);
                RosterAssignment assignment = RosterAssignment.builder()
                        .shiftDate(date)
                        .shiftType(shiftType)
                        .staff(staff)
                        .notified(false)
                        .build();
                assignments.add(rosterAssignmentRepository.save(assignment));
            }
        }

        return assignments;
    }

    /**
     * Returns staff of the given role who are available for the specified date and
     * shift.
     * Excludes:
     * 1. Staff on leave for that date
     * 2. Staff who worked the NIGHT shift the previous day (if the target shift is
     * MORNING)
     * 3. Staff already assigned to another shift on the same day
     */
    private List<Staff> getAvailableStaff(StaffRole role, LocalDate date, ShiftType shiftType) {
        List<Staff> allStaff = staffRepository.findByRole(role);

        // 1. Exclude staff on leave
        Set<Long> onLeaveIds = leaveRequestRepository.findByLeaveDate(date).stream()
                .map(lr -> lr.getStaff().getId())
                .collect(Collectors.toSet());

        // 2. Night-to-Morning constraint
        Set<Long> nightShiftYesterdayIds = new HashSet<>();
        if (shiftType == ShiftType.MORNING) {
            LocalDate yesterday = date.minusDays(1);
            nightShiftYesterdayIds = rosterAssignmentRepository
                    .findByShiftDateAndShiftType(yesterday, ShiftType.NIGHT)
                    .stream()
                    .map(a -> a.getStaff().getId())
                    .collect(Collectors.toSet());
        }

        // 3. Exclude staff already assigned to another shift on the same day
        Set<Long> alreadyAssignedToday = rosterAssignmentRepository.findByShiftDate(date).stream()
                .map(a -> a.getStaff().getId())
                .collect(Collectors.toSet());

        Set<Long> excludedIds = new HashSet<>();
        excludedIds.addAll(onLeaveIds);
        excludedIds.addAll(nightShiftYesterdayIds);
        excludedIds.addAll(alreadyAssignedToday);

        return allStaff.stream()
                .filter(s -> !excludedIds.contains(s.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Handles emergency rescheduling when a staff member takes sudden leave.
     * Removes their future assignments and fills the gaps.
     */
    @Transactional
    public void handleEmergencyReschedule(Staff staff, LocalDate fromDate) {
        log.info("🚨 Emergency rescheduling triggered for {} from {}", staff.getName(), fromDate);

        // Find all affected assignments
        List<RosterAssignment> affected = rosterAssignmentRepository.findByStaffId(staff.getId())
                .stream()
                .filter(a -> !a.getShiftDate().isBefore(fromDate))
                .toList();

        // Remove the affected assignments
        rosterAssignmentRepository.deleteAll(affected);
        log.info("   Removed {} assignments for {}", affected.size(), staff.getName());

        // Re-fill each affected shift
        for (RosterAssignment removed : affected) {
            List<Staff> available = getAvailableStaff(
                    staff.getRole(), removed.getShiftDate(), removed.getShiftType());

            if (!available.isEmpty()) {
                Collections.shuffle(available);
                Staff replacement = available.getFirst();

                RosterAssignment newAssignment = RosterAssignment.builder()
                        .shiftDate(removed.getShiftDate())
                        .shiftType(removed.getShiftType())
                        .staff(replacement)
                        .notified(true)
                        .build();
                rosterAssignmentRepository.save(newAssignment);

                // Send notification
                notificationService.notifyStaffReassignment(
                        replacement.getName(),
                        replacement.getEmail(),
                        removed.getShiftDate().toString(),
                        removed.getShiftType().name());

                log.info("   ✅ Replaced {} with {} for {} {} shift",
                        staff.getName(), replacement.getName(),
                        removed.getShiftDate(), removed.getShiftType());
            } else {
                log.error("   ❌ No replacement found for {} {} shift (role: {})",
                        removed.getShiftDate(), removed.getShiftType(), staff.getRole());
            }
        }
    }

    /**
     * Returns the roster for a specific date.
     */
    public List<RosterAssignment> getRosterByDate(LocalDate date) {
        return rosterAssignmentRepository.findByShiftDate(date);
    }

    /**
     * Returns the roster for a date range.
     */
    public List<RosterAssignment> getRosterByDateRange(LocalDate start, LocalDate end) {
        List<RosterAssignment> result = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            result.addAll(rosterAssignmentRepository.findByShiftDate(d));
        }
        return result;
    }
}
