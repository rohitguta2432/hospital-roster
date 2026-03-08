# Hospital Roster Management System — Product Requirements

## 1. Project Overview

An automated shift roster (scheduling) system for hospital staff. The system manages assignments of **Doctors**, **Nurses**, and **Wardens** across three daily shifts, ensuring minimum staffing requirements are met without violating leave requests or mandatory rest periods.

---

## 2. Staff Roles

| Role | Description |
|------|-------------|
| **Doctor** | Licensed medical professional |
| **Nurse** | Registered nursing staff |
| **Warden** | Hospital support/security personnel |

Each staff member has:
- `id` (UUID)
- `name`
- `role` (DOCTOR / NURSE / WARDEN)
- `email`
- `phone`
- `department`

---

## 3. Shift Structure

The operational day is divided into three 8-hour blocks:

| Shift | Time Range | Code |
|-------|-----------|------|
| Morning | 08:00 AM – 04:00 PM | `MORNING` |
| Evening | 04:00 PM – 12:00 AM | `EVENING` |
| Night | 12:00 AM – 08:00 AM | `NIGHT` |

---

## 4. Minimum Coverage Requirements

Every shift must meet these **minimum** staff counts:

| Role | Per Shift Minimum |
|------|:-----------------:|
| Doctors | 2 |
| Nurses | 6 |
| Wardens | 10 |

If the roster engine cannot meet minimums (e.g., too many staff on leave), it should:
1. Fill as many slots as possible
2. Flag the gap in the API response for dashboard display

---

## 5. Scheduling Constraints

### 5.1 Leave Exclusions
- Staff members on approved leave for a given date must be **excluded** from all shifts on that date.
- Applies to both **planned leave** and **emergency leave**.

### 5.2 Mandatory Rest Period (Night → Morning Rule)
- A staff member assigned to a **Night shift** (12 AM – 8 AM) must **NOT** be scheduled for the **Morning shift** (8 AM – 4 PM) the following day.
- This prevents burnout and ensures patient safety.

### 5.3 No Double-Shift
- A staff member should not be assigned to more than **one shift per day**.

---

## 6. Leave Management

### 6.1 Leave Types
| Type | Description |
|------|-------------|
| **Planned Leave** | Pre-scheduled time off (vacation, personal day) |
| **Emergency Leave** | Sudden, unplanned absence (illness, family emergency) |

### 6.2 Leave Request Fields
- `staffId` — who is requesting leave
- `startDate` / `endDate` — leave period
- `reason` — text description
- `isEmergency` — boolean flag
- `status` — PENDING / APPROVED / REJECTED

### 6.3 Emergency Leave Handling
When emergency leave is filed for a currently-scheduled staff member:
1. **Remove** the staff member from all affected roster assignments
2. **Reschedule** — run the roster engine to find a replacement
3. **Notify** — send automated notification to the newly assigned staff member

---

## 7. Roster Generation

### 7.1 Input
- `weekStartDate` — Monday of the target week
- Current staff list (excluding those on leave)
- Constraint rules (minimum coverage, rest periods)

### 7.2 Output
- 7 days × 3 shifts = **21 shift slots**
- Each slot contains a list of assigned staff with their role

### 7.3 Algorithm
1. For each day in the week:
   - Filter available staff (not on leave, not violating rest rule)
   - For each shift (Morning → Evening → Night):
     - Assign required minimums per role
     - Distribute remaining available staff fairly
2. Return the full weekly roster

---

## 8. Dashboard Metrics

The dashboard should display:
- **Total Staff** — count of all active staff
- **On Leave** — count of staff currently on leave
- **Active Assignments** — total roster assignments for the current week
- **Coverage %** — percentage of shifts meeting minimum requirements
- **Weekly Grid** — visual Mon–Sun × Morning/Evening/Night matrix

---

## 9. Non-Functional Requirements

| Requirement | Target |
|-------------|--------|
| Response time | < 500ms for all API calls |
| Roster generation | < 2s for 30 staff / 1 week |
| Database | PostgreSQL 14+ |
| Browser support | Chrome, Firefox, Edge (latest) |
| Responsive | Desktop-first (1920×1080) |
