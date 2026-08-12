# TELECOM SERVICE ASSURANCE & TROUBLE TICKET MANAGEMENT SYSTEM (TSATMS)
## Complete Technical Reference & Project Viva Presentation Guide

---

## 1. Executive Summary & Business Scenario

The **Telecom Service Assurance & Trouble Ticket Management System (TSATMS)** is an enterprise-grade Operations Support System (OSS) built using **pure Core Java SE (Java 8+)** and **JDBC**. It models how a telecommunications operator manages network outages, broadband failures, call drops, SIM issues, and service degradation across thousands of enterprise and consumer connections.

### Core Capabilities:
- **Security & Authentication**: SHA-256 password hashing with salt, math-based CAPTCHA challenge, 6-digit OTP verification, 3-attempt account locking, and login audit trails.
- **Trouble Ticket Lifecycle**: End-to-end ticket lifecycle management (OPEN → ASSIGNED → IN_PROGRESS → ESCALATED → RESOLVED → CLOSED).
- **Intelligent Engineer Dispatch**: Automated recommendation of Network Engineers using **Java 8 Stream API + Lambda + Comparators + Optional** based on specialization, region, availability, experience, and active workload.
- **Strict JDBC Transactions**: ACID-compliant ticket assignment implemented via `java.sql.Connection` (`setAutoCommit(false)`, `Savepoint`, `commit()`, `rollback()`).
- **SLA Engine & Escalation**: Real-time calculation of response/resolution SLA deadlines (`WITHIN_SLA`, `AT_RISK`, `BREACHED`) with priority-based queuing (`PriorityQueue`) for multi-level escalation.
- **Multithreaded Network Alarm Processor**: Background producer-consumer pipeline (`BlockingQueue`) that listens for network element alarms (`LINK_DOWN`, `NODE_FAILURE`) and auto-generates critical tickets.
- **Java 8 Analytics & Async Exporter**: Comprehensive stream-based analytics exportable asynchronously (`Callable`, `Future`, `ExecutorService`) to CSV and TXT files.

---

## 2. Package Architecture (`com.amdocs.telecom`)

The application follows the official Amdocs architecture guidelines:

```
com.amdocs.telecom
├── model/           # Encapsulated Domain Entities (Customer, TroubleTicket, NetworkEngineer, etc.)
├── enums/           # Type-safe Enums (CustomerType, TicketPriority, TicketStatus, SLAStatus, etc.)
├── dto/             # Data Transfer Objects (UserSessionDTO, TicketCreateDTO, DashboardMetricsDTO, etc.)
├── exception/       # Exception hierarchy (TSATMSException, AuthenticationException, etc.)
├── dao/             # Data Access Interfaces (CustomerDAO, TicketDAO, EngineerDAO, etc.)
│   └── impl/        # JDBC Implementations with PreparedStatements & Transactions
├── service/         # Business Logic Interfaces (AuthenticationService, TicketService, SLAService, etc.)
│   └── impl/        # Implementations using Stream API, Lambdas, and Collections
├── security/        # Security components (PasswordUtil SHA-256, CaptchaGenerator, OTPService)
├── scheduler/       # Multithreaded Workers (NetworkEventProcessor, SLAMonitor, ReportGenerator)
├── report/          # File Exporter (ReportExporter for CSV & TXT)
├── pattern/         # Design Pattern implementations (Factory, Strategy, Observer)
├── util/            # JDBC Connection Manager (DBConnection) & Date Utilities (DateUtil)
└── main/            # CLI Entry Point & Interactive Dashboards (Main.java)
```

---

## 3. Core Java & OOP Concepts Demonstrated

1. **Classes & Objects**: Encapsulated state representations across 12 domain entities.
2. **Encapsulation**: Private fields with getter/setter access control and constructor validation.
3. **Inheritance & Exception Hierarchy**: Custom checked exceptions extending `TSATMSException`.
4. **Abstraction & Interfaces**: Strict separation of contract (Interfaces) and implementation (`Service`, `DAO`, `Strategy`, `Observer`).
5. **Polymorphism**: Dynamic method dispatch in SLA strategy evaluation and observer notifications.
6. **Collections Framework**: `List`, `Map`, `PriorityQueue`, `ConcurrentHashMap`, `LinkedBlockingQueue`.
7. **Generics**: Generic DAOs, type-safe Collections, and `Callable<String>` / `Future<String>`.

---

## 4. Java 8 Features Deep-Dive (Viva Key Points)

### A. Stream API & Lambdas for Analytics (`ReportServiceImpl.java`)
Java 8 Streams are used for operational intelligence:
- **Group Tickets by Status**:
  ```java
  Map<TicketStatus, Long> byStatus = ticketDAO.findAll().stream()
      .collect(Collectors.groupingBy(TroubleTicket::getStatus, Collectors.counting()));
  ```
- **Filter Customers with Repeated Incidents**:
  ```java
  Map<String, Long> repeatCustomers = ticketDAO.findAll().stream()
      .collect(Collectors.groupingBy(TroubleTicket::getCustomerId, Collectors.counting()))
      .entrySet().stream()
      .filter(e -> e.getValue() > 1)
      .collect(Collectors.toMap(e -> getCustomerName(e.getKey()), Map.Entry::getValue));
  ```

### B. Stream + Lambda + Comparator + Optional (`EngineerAssignmentServiceImpl.java`)
Per requirement (PDF Page 6 & 11), engineers are recommended using:
```java
public Optional<NetworkEngineer> recommendBestEngineer(String specialization, String region) {
    return engineerDAO.findAll().stream()
        .filter(NetworkEngineer::isAvailability)
        .filter(e -> e.getSpecialization().equalsIgnoreCase(specialization) || e.getRegion().equalsIgnoreCase(region))
        .min(Comparator.comparingInt(NetworkEngineer::getActiveTicketCount)
                .thenComparing(Comparator.comparingInt(NetworkEngineer::getExperienceYears).reversed()));
}
```

### C. Top Engineers Limit Pipeline:
```java
public List<NetworkEngineer> getTopRecommendedEngineers(String specialization, int limit) {
    return engineerDAO.findAll().stream()
        .filter(NetworkEngineer::isAvailability)
        .filter(e -> e.getSpecialization().equalsIgnoreCase(specialization))
        .sorted(Comparator.comparingInt(NetworkEngineer::getActiveTicketCount)
                .thenComparing(Comparator.comparingInt(NetworkEngineer::getExperienceYears).reversed()))
        .limit(limit)
        .collect(Collectors.toList());
}
```

---

## 5. Multithreading & Concurrency Architecture

The system runs 4 concurrent multithreaded components:

| Component | Java Mechanism | Function |
| :--- | :--- | :--- |
| **NetworkEventProcessor** | `Thread` + `LinkedBlockingQueue<NetworkEvent>` | Producer-Consumer worker consuming network node alarms and auto-raising trouble tickets for critical failures. |
| **SLAMonitor** | `ScheduledExecutorService` | Periodically scans open tickets every 15s, evaluates remaining time, and updates status (`AT_RISK` / `BREACHED`). |
| **NotificationProcessor** | `Runnable` | Asynchronously dispatches SMS/email notifications to customers and engineers. |
| **ReportGenerator** | `Callable<String>` + `Future<String>` + `ExecutorService` | Computes stream analytics off the main thread and exports CSV/TXT reports non-blockingly. |

---

## 6. JDBC & Transaction Management (ACID)

To guarantee financial and operational consistency, ticket assignment is executed inside an **ACID JDBC Transaction** (`TicketDAOImpl.java`):

### Transaction Step Sequence (PDF Page 12/13):
```
Connection.setAutoCommit(false);
  ↓
1. Validate Ticket Exist & Status
  ↓
2. Validate Engineer Exist & Availability
  ↓
3. Update Ticket Assignment & Status ('ASSIGNED')
  ↓
4. Increment Engineer Active Workload Count
  ↓
5. Insert Ticket Status History Audit Record
  ↓
6. Insert Notification Entry for Engineer
  ↓
7. Insert System Audit Log Entry
  ↓
Connection.commit(); (ROLLBACK to Savepoint on any SQLException)
```

---

## 7. Design Patterns Implemented

1. **DAO Pattern**: Decouples business logic from SQLite persistence interfaces (`CustomerDAO`, `TicketDAO`, `EngineerDAO`).
2. **Singleton Pattern**: Ensures single shared instances for `DBConnection` and `CaptchaGenerator`.
3. **Factory Pattern**: `NotificationFactory` constructs typed notifications (`TICKET_CREATION`, `ENGINEER_ASSIGNMENT`, `SLA_BREACH`).
4. **Strategy Pattern**: `SLAStrategy` defines pluggable SLA calculation rules (`DefaultSLAStrategy`).
5. **Observer Pattern**: `TicketSubject` notifies `NotificationObserver` whenever a ticket's status changes.

---

## 8. Database Schema & Normalized SQL

The project embeds **SQLite JDBC**, creating 12 normalized relational tables automatically on first startup:

- `customers` (PK: `customer_id`, Unique: `customer_number`)
- `telecom_services` (PK: `service_id`, FK: `customer_id`)
- `network_engineers` (PK: `engineer_id`, Unique: `employee_code`)
- `sla_configs` (PK: `sla_id`, Unique: `priority`)
- `trouble_tickets` (PK: `ticket_id`, Unique: `ticket_number`, FK: `customer_id`, `service_id`, `assigned_engineer_id`)
- `ticket_status_history` (PK: `history_id`, FK: `ticket_id`)
- `escalation_history` (PK: `escalation_id`, FK: `ticket_id`)
- `network_events` (PK: `event_id`)
- `notifications` (PK: `notification_id`)
- `feedback` (PK: `feedback_id`, FK: `ticket_id`)
- `audit_logs` (PK: `audit_id`)
- `login_history` & `user_credentials` (PK: `username`)

---

## 9. Role-Based Dashboards & Login Credentials

### Default Test Credentials:

| Role | Username | Password | Access Capabilities |
| :--- | :--- | :--- | :--- |
| **Customer** | `CUST100245` | `Password@123` | View active services, raise ticket, track ticket status & SLA, view history, submit feedback. |
| **Service Desk** | `ADMIN01` | `Password@123` | View open tickets, assign engineer via Stream recommender, escalate via PriorityQueue, close ticket, generate reports. |
| **Network Engineer** | `ENG1008` | `Password@123` | View assigned tickets, update root cause & resolution code, mark resolved. |
| **Network Manager** | `MGR01` | `Password@123` | View KPI metrics summary dashboard, run Java 8 Stream analytics report, simulate network fault events. |

---

## 10. How to Compile & Run

### Prerequisites:
- JDK 8 or higher (Java 26 verified).
- Terminal or Command Prompt.

### Step 1: Compile the Codebase
Run the compilation batch script:
```cmd
compile.bat
```
*(Alternatively, execute: `javac -cp "lib/sqlite-jdbc.jar" -d bin src/com/amdocs/telecom/**/*.java`)*

### Step 2: Run the System
Launch the interactive CLI application:
```cmd
run.bat
```
*(Alternatively, execute: `java -cp "bin;lib/sqlite-jdbc.jar" com.amdocs.telecom.main.Main`)*

---

## 11. Sample Viva / Interview Questions & Answers

### Q1: How did you implement engineer assignment without external frameworks?
> **Answer**: We implemented a custom `EngineerAssignmentService` using **Java 8 Stream API**. It filters available engineers, matches their specialization or region, sorts them by active workload ascending (and experience descending), and picks the optimal candidate using `.min(Comparator)`. The assignment is then executed inside an explicit JDBC transaction with `setAutoCommit(false)` and `commit()/rollback()`.

### Q2: How is transaction safety guaranteed during ticket assignment?
> **Answer**: In `TicketDAOImpl.assignEngineerTransaction`, we pass the active JDBC `Connection`. We disable auto-commit, create a `Savepoint`, validate ticket and engineer availability, update the ticket status, update the engineer's active ticket count, insert history and audit records. If all queries succeed, `conn.commit()` is called. If any step throws an `SQLException`, `conn.rollback(savepoint)` is executed to prevent partial data updates.

### Q3: How does the multithreaded network event processor work?
> **Answer**: We use the **Producer-Consumer pattern** with a thread-safe `LinkedBlockingQueue<NetworkEvent>`. When network elements emit alarms (produced via option 6 or auto-simulation), they enter the queue. A background daemon thread running `NetworkEventProcessor` continuously takes events from the queue, inspects alarm severity, and automatically creates CRITICAL trouble tickets for severe faults.

### Q4: How is the PriorityQueue used in escalation management?
> **Answer**: `TroubleTicket` implements `Comparable<TroubleTicket>`. In `EscalationServiceImpl`, tickets queued for escalation enter a `PriorityQueue`. When processed via `.poll()`, tickets with `CRITICAL` priority are automatically dequeued before `HIGH`, `MEDIUM`, or `LOW` priority tickets, ensuring urgent incidents are escalated to management first.
