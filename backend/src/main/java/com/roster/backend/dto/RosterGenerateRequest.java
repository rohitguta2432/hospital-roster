package com.roster.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RosterGenerateRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
