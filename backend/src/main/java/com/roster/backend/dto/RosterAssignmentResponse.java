package com.roster.backend.dto;

import com.roster.backend.model.ShiftType;
import com.roster.backend.model.StaffRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RosterAssignmentResponse {
    private Long id;
    private LocalDate shiftDate;
    private ShiftType shiftType;
    private Long staffId;
    private String staffName;
    private String staffEmail;
    private StaffRole staffRole;
    private boolean notified;
}
