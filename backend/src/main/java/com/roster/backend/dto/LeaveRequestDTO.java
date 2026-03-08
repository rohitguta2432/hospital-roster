package com.roster.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestDTO {
    private Long staffId;
    private LocalDate leaveDate;
    private boolean emergency;
}
