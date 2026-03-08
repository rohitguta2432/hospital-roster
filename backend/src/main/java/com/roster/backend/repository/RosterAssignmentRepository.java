package com.roster.backend.repository;

import com.roster.backend.model.RosterAssignment;
import com.roster.backend.model.ShiftType;
import com.roster.backend.model.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RosterAssignmentRepository extends JpaRepository<RosterAssignment, Long> {

    List<RosterAssignment> findByShiftDateAndShiftType(LocalDate shiftDate, ShiftType shiftType);

    List<RosterAssignment> findByShiftDate(LocalDate shiftDate);

    List<RosterAssignment> findByStaffId(Long staffId);

    @Query("SELECT ra FROM RosterAssignment ra WHERE ra.shiftDate = :date AND ra.shiftType = :shiftType AND ra.staff.role = :role")
    List<RosterAssignment> findByDateShiftTypeAndRole(
            @Param("date") LocalDate date,
            @Param("shiftType") ShiftType shiftType,
            @Param("role") StaffRole role);

    @Query("SELECT ra FROM RosterAssignment ra WHERE ra.shiftDate = :date AND ra.shiftType = :shiftType AND ra.staff.id = :staffId")
    List<RosterAssignment> findByDateShiftTypeAndStaff(
            @Param("date") LocalDate date,
            @Param("shiftType") ShiftType shiftType,
            @Param("staffId") Long staffId);

    void deleteByStaffIdAndShiftDateGreaterThanEqual(Long staffId, LocalDate fromDate);
}
