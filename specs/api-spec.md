# Hospital Roster — REST API Specification

**Base URL:** `http://localhost:8080/api`

---

## Staff Endpoints

### `GET /api/staff`
List all staff members.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Dr. Priya Sharma",
    "role": "DOCTOR",
    "email": "priya.sharma@hospital.com",
    "phone": "+91-9876543210",
    "department": "Cardiology"
  }
]
```

### `POST /api/staff`
Create a new staff member.

**Request Body:**
```json
{
  "name": "Dr. Priya Sharma",
  "role": "DOCTOR",
  "email": "priya.sharma@hospital.com",
  "phone": "+91-9876543210",
  "department": "Cardiology"
}
```

**Response:** `200 OK` — returns the created staff object.

### `DELETE /api/staff/{id}`
Delete a staff member by ID.

**Response:** `204 No Content`

---

## Roster Endpoints

### `GET /api/roster?weekStart=2026-03-09`
Get roster assignments for a given week.

**Query Parameters:**
| Param | Type | Required | Description |
|-------|------|----------|-------------|
| `weekStart` | `date` (ISO) | Yes | Monday of the target week |

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "staffName": "Dr. Priya Sharma",
    "staffRole": "DOCTOR",
    "shiftType": "MORNING",
    "date": "2026-03-09"
  }
]
```

### `POST /api/roster/generate`
Generate a weekly roster.

**Request Body:**
```json
{
  "weekStartDate": "2026-03-09"
}
```

**Response:** `200 OK` — returns array of `RosterAssignment` objects.

---

## Leave Endpoints

### `GET /api/leaves`
List all leave requests.

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "staffId": 5,
    "staffName": "Nurse Anita Roy",
    "startDate": "2026-03-10",
    "endDate": "2026-03-12",
    "reason": "Family emergency",
    "isEmergency": true,
    "status": "APPROVED"
  }
]
```

### `POST /api/leaves`
Submit a new leave request.

**Request Body:**
```json
{
  "staffId": 5,
  "startDate": "2026-03-10",
  "endDate": "2026-03-12",
  "reason": "Family emergency",
  "isEmergency": true
}
```

**Response:** `200 OK` — returns the created leave request.

### `PUT /api/leaves/{id}/approve`
Approve a pending leave request.

**Response:** `200 OK`

### `PUT /api/leaves/{id}/reject`
Reject a pending leave request.

**Response:** `200 OK`

---

## Error Responses

All endpoints return standard error format:

```json
{
  "timestamp": "2026-03-08T14:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Staff member not found with id: 99"
}
```

| Status | Meaning |
|--------|---------|
| `400` | Invalid request body or parameters |
| `404` | Resource not found |
| `500` | Internal server error |
