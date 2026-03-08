# 🏥 Hospital Roster Management System

A full-stack hospital staff roster management system built with **Spring Boot** and **Next.js**.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Spring Boot 3.5.0, Java 21, Spring Data JPA |
| **Database** | PostgreSQL 14 |
| **Frontend** | Next.js 15, React 19 |
| **Styling** | CSS (Dark Navy Glassmorphism) |

## Features

- 📊 **Dashboard** — Weekly roster overview with coverage metrics
- 👥 **Staff Directory** — Manage doctors, nurses, and wardens
- 📅 **Leave Management** — Submit and track leave requests (planned & emergency)
- 🔄 **Roster Generation** — Constraint-based scheduling engine (Morning/Evening/Night shifts)
- 🚨 **Emergency Rescheduling** — Automatic reassignment on emergency leave

## Getting Started

### Prerequisites
- Java 21+
- Node.js 18+
- PostgreSQL 14+

### Backend
```bash
cd backend
./mvnw spring-boot:run
# Runs on http://localhost:8080
```

### Frontend
```bash
cd frontend
npm install
npm run dev -- --port 3006
# Runs on http://localhost:3006
```

### Database
The app auto-creates tables via Hibernate and seeds 30 demo staff on first run.

## Project Structure
```
roaster/
├── backend/          # Spring Boot API
│   └── src/main/java/com/roster/backend/
│       ├── controller/   # REST endpoints
│       ├── model/        # JPA entities
│       ├── repository/   # Data access
│       ├── service/      # Business logic
│       ├── dto/          # Data transfer objects
│       └── config/       # DataLoader, CORS
├── frontend/         # Next.js UI
│   └── src/app/
│       ├── dashboard/
│       ├── staff/
│       └── leave/
└── README.md
```

## License
MIT
