package com.roster.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    /**
     * Sends an email notification to a staff member about a shift reassignment.
     * Currently a mock implementation that logs the notification.
     * Replace with actual email sending (e.g., Spring Mail, SendGrid) in
     * production.
     */
    public void notifyStaffReassignment(String staffName, String staffEmail,
            String shiftDate, String shiftType) {
        log.info("📧 EMAIL NOTIFICATION SENT:");
        log.info("   To: {} <{}>", staffName, staffEmail);
        log.info("   Subject: Shift Reassignment Notice");
        log.info("   Body: You have been assigned to the {} shift on {}.", shiftType, shiftDate);
        log.info("   Please acknowledge this assignment at your earliest convenience.");
    }
}
