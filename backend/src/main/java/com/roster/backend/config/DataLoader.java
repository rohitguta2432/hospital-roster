package com.roster.backend.config;

import com.roster.backend.model.Staff;
import com.roster.backend.model.StaffRole;
import com.roster.backend.repository.StaffRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final StaffRepository staffRepository;

    public DataLoader(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public void run(String... args) {
        if (staffRepository.count() > 0) {
            System.out.println("📦 Data already seeded. Skipping...");
            return;
        }

        System.out.println("🌱 Seeding demo data...");

        List<Staff> demoStaff = List.of(
                // Doctors (5)
                createStaff("Dr. Arun Mehta", "arun.mehta@hospital.com", StaffRole.DOCTOR),
                createStaff("Dr. Priya Sharma", "priya.sharma@hospital.com", StaffRole.DOCTOR),
                createStaff("Dr. Rajiv Kumar", "rajiv.kumar@hospital.com", StaffRole.DOCTOR),
                createStaff("Dr. Sunita Patel", "sunita.patel@hospital.com", StaffRole.DOCTOR),
                createStaff("Dr. Vikram Singh", "vikram.singh@hospital.com", StaffRole.DOCTOR),

                // Nurses (10)
                createStaff("Nurse Anjali Das", "anjali.das@hospital.com", StaffRole.NURSE),
                createStaff("Nurse Bina Rao", "bina.rao@hospital.com", StaffRole.NURSE),
                createStaff("Nurse Chitra Nair", "chitra.nair@hospital.com", StaffRole.NURSE),
                createStaff("Nurse Deepa Joshi", "deepa.joshi@hospital.com", StaffRole.NURSE),
                createStaff("Nurse Esha Verma", "esha.verma@hospital.com", StaffRole.NURSE),
                createStaff("Nurse Fatima Khan", "fatima.khan@hospital.com", StaffRole.NURSE),
                createStaff("Nurse Geeta Reddy", "geeta.reddy@hospital.com", StaffRole.NURSE),
                createStaff("Nurse Hema Iyer", "hema.iyer@hospital.com", StaffRole.NURSE),
                createStaff("Nurse Indira Roy", "indira.roy@hospital.com", StaffRole.NURSE),
                createStaff("Nurse Jaya Pillai", "jaya.pillai@hospital.com", StaffRole.NURSE),

                // Wardens (15)
                createStaff("Warden Kiran Bhatt", "kiran.bhatt@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Lakshmi Menon", "lakshmi.menon@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Manoj Tiwari", "manoj.tiwari@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Neha Gupta", "neha.gupta@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Om Prakash", "om.prakash@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Pooja Saxena", "pooja.saxena@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Rahul Mishra", "rahul.mishra@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Sita Pandey", "sita.pandey@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Tarun Agarwal", "tarun.agarwal@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Uma Chopra", "uma.chopra@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Vinod Shah", "vinod.shah@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Waseem Ali", "waseem.ali@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Yogesh Kapoor", "yogesh.kapoor@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Zara Begum", "zara.begum@hospital.com", StaffRole.WARDEN),
                createStaff("Warden Anil Deshmukh", "anil.deshmukh@hospital.com", StaffRole.WARDEN));

        staffRepository.saveAll(demoStaff);
        System.out.println("✅ Seeded " + demoStaff.size() + " staff members (5 Doctors, 10 Nurses, 15 Wardens)");
    }

    private Staff createStaff(String name, String email, StaffRole role) {
        Staff s = new Staff();
        s.setName(name);
        s.setEmail(email);
        s.setRole(role);
        return s;
    }
}
