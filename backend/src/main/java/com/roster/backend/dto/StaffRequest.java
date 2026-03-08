package com.roster.backend.dto;

import com.roster.backend.model.StaffRole;
import lombok.Data;

@Data
public class StaffRequest {
    private String name;
    private String email;
    private StaffRole role;
}
