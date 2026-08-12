# Telecom Service Assurance & Trouble Ticket Management System (TSATMS)

An enterprise-grade Operations Support System (OSS) built using **pure Core Java SE** and **JDBC** for managing network outages, broadband failures, call drops, and service degradation in a telecom environment.

## Tech Stack
- **Language**: Java SE (Java 8+ compatible, tested on Java 26)
- **Database**: SQLite (embedded, via JDBC — auto-created on first run)
- **Frameworks**: None — Pure Java with JDBC only
- **Build**: Batch scripts (no Maven/Gradle required)

## Features
- SHA-256 password hashing, CAPTCHA, OTP-based 2FA, account locking
- Full Trouble Ticket lifecycle (OPEN → ASSIGNED → IN_PROGRESS → ESCALATED → RESOLVED → CLOSED)
- Intelligent engineer dispatch using Java 8 Streams, Lambdas, Comparators, and Optional
- ACID-compliant JDBC transactions with Savepoints and Rollback
- Real-time SLA monitoring with `ScheduledExecutorService`
- Multithreaded network alarm processing (`BlockingQueue` producer-consumer)
- Async report generation (`Callable`, `Future`, `ExecutorService`)
- PriorityQueue-based ticket escalation
- 4 role-based dashboards (Customer, Service Desk, Network Engineer, Network Manager)
- 5 design patterns (DAO, Singleton, Factory, Strategy, Observer)

## Prerequisites
- JDK 8 or higher

## How to Run

### Step 1: Compile
```cmd
compile.bat
```

### Step 2: Run
```cmd
run.bat
```

### Default Login Credentials
| Role | Username | Password |
|------|----------|----------|
| Customer | CUST100245 | Password@123 |
| Service Desk | ADMIN01 | Password@123 |
| Network Engineer | ENG1008 | Password@123 |
| Network Manager | MGR01 | Password@123 |

## Project Structure
```
src/com/amdocs/telecom/
├── enums/       # Type-safe enums (CustomerType, TicketPriority, TicketStatus, etc.)
├── model/       # Domain entities (Customer, TroubleTicket, NetworkEngineer, etc.)
├── dto/         # Data Transfer Objects (UserSessionDTO, TicketCreateDTO, etc.)
├── exception/   # Custom exception hierarchy
├── dao/         # DAO interfaces
│   └── impl/    # JDBC implementations with PreparedStatements
├── service/     # Business logic interfaces
│   └── impl/    # Implementations using Streams, Lambdas, Transactions
├── security/    # SHA-256 hashing, CAPTCHA, OTP
├── scheduler/   # Multithreaded workers (SLAMonitor, NetworkEventProcessor, ReportGenerator)
├── report/      # CSV/TXT report exporter
├── pattern/     # Design pattern implementations (Factory, Strategy, Observer)
├── util/        # DB connection manager & date utilities
└── main/        # CLI entry point & interactive dashboards
lib/             # SQLite JDBC driver JARs
```

## Database
SQLite database (`tsatms.db`) is auto-created on first run with 12 normalized tables and sample seed data. No manual database setup required.

## Documentation
See [PROJECT_EXPLANATION.md](PROJECT_EXPLANATION.md) for a detailed technical reference and viva/interview preparation guide.
