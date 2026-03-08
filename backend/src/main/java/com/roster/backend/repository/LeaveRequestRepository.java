package com.roster.backend.repository;

import com.roster.backend.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByLeaveDate(LocalDate leaveDate);

    List<LeaveRequest> findByStaffIdAndLeaveDate(Long staffId, LocalDate leaveDate);

    List<LeaveRequest> findByStaffId(Long staffId);
}
