package com.roster.backend.repository;

import com.roster.backend.model.Staff;
import com.roster.backend.model.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByRole(StaffRole role);
}
