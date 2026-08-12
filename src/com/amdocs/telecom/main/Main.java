package com.amdocs.telecom.main;

import com.amdocs.telecom.dao.impl.ServiceDAOImpl;
import com.amdocs.telecom.dao.impl.TicketDAOImpl;
import com.amdocs.telecom.dto.*;
import com.amdocs.telecom.enums.*;
import com.amdocs.telecom.exception.AuthenticationException;
import com.amdocs.telecom.model.*;
import com.amdocs.telecom.scheduler.NetworkEventProcessor;
import com.amdocs.telecom.scheduler.ReportGenerator;
import com.amdocs.telecom.scheduler.SLAMonitor;
import com.amdocs.telecom.security.CaptchaGenerator;
import com.amdocs.telecom.service.*;
import com.amdocs.telecom.service.impl.*;
import com.amdocs.telecom.util.DBConnection;
import com.amdocs.telecom.util.DateUtil;

import java.util.*;
import java.util.concurrent.Future;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthenticationService authService = new AuthenticationServiceImpl();
    private static final TicketService ticketService = new TicketServiceImpl();
    private static final EngineerAssignmentService engineerService = new EngineerAssignmentServiceImpl();
    private static final SLAService slaService = new SLAServiceImpl();
    private static final EscalationService escalationService = new EscalationServiceImpl();
    private static final ReportService reportService = new ReportServiceImpl();
    private static final NotificationService notificationService = new NotificationServiceImpl();

    private static Thread eventThread;
    private static SLAMonitor slaMonitor;

    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("     TELECOM SERVICE ASSURANCE SYSTEM (TSATMS)      ");
        System.out.println("====================================================");

        // 1. Initialize SQLite JDBC Relational Database & Seed Data
        DBConnection.initializeDatabase();

        // 2. Start Background Multithreading Services
        startBackgroundWorkers();

        // 3. Launch Role-based Interactive Main Login Menu
        boolean running = true;
        while (running) {
            System.out.println("\n---------------- MAIN MENU ----------------");
            System.out.println("1. Customer Login");
            System.out.println("2. Service Desk Login");
            System.out.println("3. Network Engineer Login");
            System.out.println("4. Network Manager Login");
            System.out.println("5. Forgot Password");
            System.out.println("6. Simulate Incoming Network Fault Event");
            System.out.println("7. Exit System");
            System.out.print("Enter choice (1-7): ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    handleRoleLogin(UserRole.CUSTOMER, "CUST100245");
                    break;
                case "2":
                    handleRoleLogin(UserRole.SERVICE_DESK, "ADMIN01");
                    break;
                case "3":
                    handleRoleLogin(UserRole.NETWORK_ENGINEER, "ENG1008");
                    break;
                case "4":
                    handleRoleLogin(UserRole.NETWORK_MANAGER, "MGR01");
                    break;
                case "5":
                    handleForgotPassword();
                    break;
                case "6":
                    simulateNetworkEvent();
                    break;
                case "7":
                    running = false;
                    System.out.println("\nShutting down TSATMS... Goodbye!");
                    stopBackgroundWorkers();
                    break;
                default:
                    System.out.println("Invalid option! Please select 1-7.");
            }
        }
    }

    private static void startBackgroundWorkers() {
        NetworkEventProcessor eventProcessor = new NetworkEventProcessor();
        eventThread = new Thread(eventProcessor, "NetworkEventWorker");
        eventThread.setDaemon(true);
        eventThread.start();

        slaMonitor = new SLAMonitor();
        slaMonitor.startMonitoring();
    }

    private static void stopBackgroundWorkers() {
        if (slaMonitor != null) slaMonitor.stopMonitoring();
    }

    private static void handleRoleLogin(UserRole role, String defaultUsername) {
        System.out.println("\n--- " + role + " LOGIN ---");
        System.out.print("Enter Username (Default '" + defaultUsername + "'): ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) username = defaultUsername;

        System.out.print("Enter Password (Default 'Password@123'): ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) password = "Password@123";

        // Generate CAPTCHA Challenge
        CaptchaGenerator.CaptchaChallenge challenge = authService.generateCaptcha();
        System.out.println("[SECURITY CHECK] " + challenge.getQuestion());
        System.out.print("Answer CAPTCHA: ");
        String captchaInput = scanner.nextLine().trim();

        // OTP Generation
        String otp = authService.sendOTP(username);
        System.out.println("[OTP SECURITY] Simulated SMS sent! Your 6-Digit OTP is: " + otp);
        System.out.print("Enter OTP Code: ");
        String otpInput = scanner.nextLine().trim();

        try {
            UserSessionDTO session = authService.login(username, password, captchaInput, challenge.getAnswer(), otpInput, role);
            System.out.println("\n>> LOGIN SUCCESSFUL! Welcome " + username + " (" + role + ")");
            routeToDashboard(session);
        } catch (AuthenticationException e) {
            System.err.println("LOGIN FAILED: " + e.getMessage());
        }
    }

    private static void routeToDashboard(UserSessionDTO session) {
        switch (session.getRole()) {
            case CUSTOMER:
                runCustomerDashboard(session);
                break;
            case SERVICE_DESK:
                runServiceDeskDashboard(session);
                break;
            case NETWORK_ENGINEER:
                runEngineerDashboard(session);
                break;
            case NETWORK_MANAGER:
                runManagerDashboard(session);
                break;
        }
    }

    // ---------------- 13. CUSTOMER DASHBOARD ----------------
    private static void runCustomerDashboard(UserSessionDTO session) {
        boolean active = true;
        while (active) {
            System.out.println("\n========== CUSTOMER DASHBOARD ==========");
            System.out.println("1. View Active Services");
            System.out.println("2. Raise Trouble Ticket");
            System.out.println("3. View My Tickets");
            System.out.println("4. Track Ticket");
            System.out.println("5. View Ticket History");
            System.out.println("6. View Notifications");
            System.out.println("7. Submit Feedback");
            System.out.println("8. Logout");
            System.out.print("Select action (1-8): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.println("\n--- Active Telecom Services ---");
                    ServiceDAOImpl serviceDAO = new ServiceDAOImpl();
                    List<TelecomService> services = serviceDAO.findByCustomerId(session.getAssociatedEntityId());
                    if (services.isEmpty()) {
                        System.out.println("No active services found.");
                    } else {
                        services.forEach(s -> System.out.printf("Service ID: %d | Code: %s | Name: %s | Type: %s | Status: %s\n",
                                s.getServiceId(), s.getServiceCode(), s.getServiceName(), s.getServiceType(), s.getServiceStatus()));
                    }
                    break;
                case "2":
                    System.out.println("\n--- Raise Trouble Ticket ---");
                    System.out.print("Select Service ID (e.g. 501): ");
                    int serviceId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.println("Categories: NETWORK_OUTAGE, CALL_DROP, SLOW_DATA, NO_CONNECTIVITY, SIM_ISSUE, BILLING, BROADBAND");
                    System.out.print("Enter Category: ");
                    IncidentCategory category = IncidentCategory.valueOf(scanner.nextLine().trim().toUpperCase());
                    System.out.print("Enter Description: ");
                    String desc = scanner.nextLine().trim();
                    System.out.print("Priority (LOW, MEDIUM, HIGH, CRITICAL): ");
                    TicketPriority priority = TicketPriority.valueOf(scanner.nextLine().trim().toUpperCase());

                    TicketCreateDTO dto = new TicketCreateDTO(session.getAssociatedEntityId(), serviceId, category, desc, priority, priority.name());
                    TroubleTicket created = ticketService.createTicket(dto);
                    System.out.println(">> TICKET CREATED SUCCESSFULLY!");
                    System.out.println("   Ticket Number : " + created.getTicketNumber());
                    System.out.println("   SLA Deadline  : " + created.getSlaDeadline());
                    break;
                case "3":
                    System.out.println("\n--- My Trouble Tickets ---");
                    List<TroubleTicket> myTickets = ticketService.getCustomerTickets(session.getAssociatedEntityId());
                    myTickets.forEach(System.out::println);
                    break;
                case "4":
                    System.out.print("Enter Ticket Number (e.g. TT-2026-004521): ");
                    String ticketNum = scanner.nextLine().trim();
                    try {
                        TroubleTicket ticket = ticketService.getTicketByNumber(ticketNum);
                        System.out.println("\n--- Ticket Details ---");
                        System.out.println("Ticket Number : " + ticket.getTicketNumber());
                        System.out.println("Priority      : " + ticket.getPriority());
                        System.out.println("Status        : " + ticket.getStatus());
                        System.out.println("Engineer ID   : " + (ticket.getAssignedEngineerId() != null ? ticket.getAssignedEngineerId() : "Not Assigned Yet"));
                        System.out.println("SLA Deadline  : " + ticket.getSlaDeadline());
                        System.out.println("SLA Status    : " + ticket.getSlaStatus());
                        System.out.println("Remaining Time: " + slaService.getRemainingTimeMinutes(ticket.getSlaDeadline()) + " mins");
                        if (ticket.getResolutionDetails() != null) {
                            System.out.println("Resolution    : " + ticket.getResolutionDetails());
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case "5":
                    System.out.print("Enter Ticket ID: ");
                    int tid = Integer.parseInt(scanner.nextLine().trim());
                    TicketDAOImpl tdao = new TicketDAOImpl();
                    List<TicketStatusHistory> historyList = tdao.getTicketHistory(tid);
                    System.out.println("\n--- Status History ---");
                    historyList.forEach(h -> System.out.printf("[%s] %s -> %s by %s | %s\n",
                            h.getChangedDate(), h.getOldStatus(), h.getNewStatus(), h.getChangedBy(), h.getRemarks()));
                    break;
                case "6":
                    System.out.println("\n--- Notifications ---");
                    List<Notification> notifs = notificationService.getNotificationsForUser(String.valueOf(session.getAssociatedEntityId()));
                    notifs.forEach(n -> System.out.printf("[%s] %s | %s\n", n.getCreatedDate(), n.getNotificationType(), n.getMessage()));
                    break;
                case "7":
                    System.out.println("Thank you for your feedback submission!");
                    break;
                case "8":
                    active = false;
                    break;
            }
        }
    }

    // ---------------- 14. SERVICE DESK DASHBOARD ----------------
    private static void runServiceDeskDashboard(UserSessionDTO session) {
        boolean active = true;
        while (active) {
            System.out.println("\n========== SERVICE DESK DASHBOARD ==========");
            System.out.println("1. View Open Tickets");
            System.out.println("2. Assign Engineer (Transactional & Stream Recommender)");
            System.out.println("3. Reassign Ticket");
            System.out.println("4. Escalate Ticket (PriorityQueue)");
            System.out.println("5. Update Priority");
            System.out.println("6. Monitor SLA");
            System.out.println("7. Close Ticket");
            System.out.println("8. Generate Async Reports (Callable / Future)");
            System.out.println("9. Logout");
            System.out.print("Select action (1-9): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.println("\n--- Open Tickets ---");
                    ticketService.getOpenTickets().forEach(System.out::println);
                    break;
                case "2":
                    System.out.println("\n--- Assign Engineer ---");
                    System.out.print("Enter Ticket ID to Assign: ");
                    int ticketId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter Specialization needed (e.g. Core Network, Broadband, RAN, IP Network): ");
                    String spec = scanner.nextLine().trim();

                    System.out.println("\n[STREAM RECOMMENDER] Top matching engineers with lowest workload:");
                    List<NetworkEngineer> recommended = engineerService.getTopRecommendedEngineers(spec, 3);
                    recommended.forEach(System.out::println);

                    if (recommended.isEmpty()) {
                        System.out.println("No matching available engineers found! Attempting default assignment.");
                        break;
                    }

                    int selectedEngId = recommended.get(0).getEngineerId();
                    System.out.println("Auto-selecting Engineer #" + selectedEngId + " (" + recommended.get(0).getEngineerName() + ")");

                    try {
                        // Executes ACID Transaction (COMMIT / ROLLBACK)
                        boolean assigned = engineerService.assignEngineerToTicketTransactional(ticketId, selectedEngId, session.getUsername());
                        if (assigned) {
                            System.out.println(">> TRANSACTION COMMITTED: Engineer assigned & status updated successfully!");
                        }
                    } catch (Exception e) {
                        System.err.println("TRANSACTION FAILED & ROLLED BACK: " + e.getMessage());
                    }
                    break;
                case "3":
                    System.out.println("Reassigning ticket to another engineer...");
                    break;
                case "4":
                    System.out.println("\n--- Escalate Ticket ---");
                    System.out.print("Enter Ticket ID to Queue for Escalation: ");
                    int escTid = Integer.parseInt(scanner.nextLine().trim());
                    try {
                        TroubleTicket t = ticketService.getTicketById(escTid);
                        escalationService.queueTicketForEscalation(t);
                        System.out.print("Processing escalation queue (PriorityQueue)... ");
                        escalationService.processNextEscalation(session.getUsername(), "SLA threshold exceeded / High severity incident");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case "5":
                    System.out.print("Enter Ticket ID: ");
                    int pid = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("New Priority (LOW, MEDIUM, HIGH, CRITICAL): ");
                    TicketPriority p = TicketPriority.valueOf(scanner.nextLine().trim().toUpperCase());
                    try {
                        ticketService.updatePriority(pid, p);
                        System.out.println("Priority updated successfully!");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case "6":
                    System.out.println("\n--- SLA Status Monitor ---");
                    ticketService.getAllTickets().forEach(t ->
                            System.out.printf("Ticket #%s | Priority: %s | Status: %s | SLA Status: %s | Deadline: %s\n",
                                    t.getTicketNumber(), t.getPriority(), t.getStatus(), t.getSlaStatus(), t.getSlaDeadline()));
                    break;
                case "7":
                    System.out.print("Enter Ticket ID to Close: ");
                    int cid = Integer.parseInt(scanner.nextLine().trim());
                    try {
                        ticketService.closeTicket(cid, session.getUsername(), "Closed after customer confirmation.");
                        System.out.println("Ticket CLOSED successfully.");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case "8":
                    System.out.println("\n--- Generating Async Analytical Report (Callable / Future) ---");
                    Future<String> reportFuture = ReportGenerator.generateAsyncReport();
                    try {
                        String result = reportFuture.get(); // Waits for async Future completion
                        System.out.println(result);
                    } catch (Exception e) {
                        System.err.println("Report Generation Failed: " + e.getMessage());
                    }
                    break;
                case "9":
                    active = false;
                    break;
            }
        }
    }

    // ---------------- 15. NETWORK MANAGER DASHBOARD ----------------
    private static void runManagerDashboard(UserSessionDTO session) {
        boolean active = true;
        while (active) {
            DashboardMetricsDTO metrics = ticketService.getDashboardMetrics();
            metrics.displayNetworkManagerDashboard();

            System.out.println("\nManager Options:");
            System.out.println("1. View Java 8 Stream Analytics Report");
            System.out.println("2. View All Tickets Across Regions");
            System.out.println("3. Trigger Simulated Network Fault Alarm");
            System.out.println("4. Logout");
            System.out.print("Select choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.println(reportService.generateFullReportText());
                    break;
                case "2":
                    ticketService.getAllTickets().forEach(System.out::println);
                    break;
                case "3":
                    simulateNetworkEvent();
                    break;
                case "4":
                    active = false;
                    break;
            }
        }
    }

    // ---------------- NETWORK ENGINEER DASHBOARD ----------------
    private static void runEngineerDashboard(UserSessionDTO session) {
        boolean active = true;
        while (active) {
            System.out.println("\n========== NETWORK ENGINEER DASHBOARD ==========");
            System.out.println("1. View My Assigned Tickets");
            System.out.println("2. Update Incident Resolution & Root Cause");
            System.out.println("3. Logout");
            System.out.print("Select choice: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.println("\n--- My Assigned Tickets ---");
                    ticketService.getEngineerTickets(session.getAssociatedEntityId()).forEach(System.out::println);
                    break;
                case "2":
                    System.out.println("\n--- Incident Resolution ---");
                    System.out.print("Enter Ticket ID to Resolve: ");
                    int tid = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter Root Cause: ");
                    String rootCause = scanner.nextLine().trim();
                    System.out.print("Enter Resolution Details: ");
                    String details = scanner.nextLine().trim();
                    System.out.println("Resolution Codes: HARDWARE_FAILURE, CONFIGURATION_ERROR, NETWORK_CONGESTION, SOFTWARE_FAILURE, FIBER_CUT, POWER_FAILURE, CUSTOMER_DEVICE, UNKNOWN");
                    System.out.print("Enter Resolution Code: ");
                    ResolutionCode code = ResolutionCode.valueOf(scanner.nextLine().trim().toUpperCase());

                    TicketResolutionDTO dto = new TicketResolutionDTO(tid, rootCause, details, code, session.getUsername());
                    try {
                        boolean resolved = ticketService.resolveTicket(dto);
                        if (resolved) System.out.println(">> TICKET RESOLVED SUCCESSFULLY!");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case "3":
                    active = false;
                    break;
            }
        }
    }

    private static void handleForgotPassword() {
        System.out.println("\n--- FORGOT PASSWORD RESET ---");
        System.out.print("Enter Username: ");
        String uname = scanner.nextLine().trim();
        String otp = authService.sendOTP(uname);
        System.out.println("[OTP SECURITY] Reset Code sent: " + otp);
        System.out.print("Enter OTP Code: ");
        String userOtp = scanner.nextLine().trim();
        if (otp.equals(userOtp)) {
            System.out.print("Enter New Password: ");
            String newPass = scanner.nextLine().trim();
            try {
                authService.resetPassword(uname, newPass);
                System.out.println(">> PASSWORD RESET SUCCESSFUL! You can now log in.");
            } catch (Exception e) {
                System.out.println("Reset error: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid OTP Code!");
        }
    }

    private static void simulateNetworkEvent() {
        System.out.println("\n--- SIMULATING INCOMING NETWORK FAULT EVENT ---");
        NetworkEvent event = new NetworkEvent(
                "NE-" + (100000 + new Random().nextInt(900000)),
                "MUM-RAN-" + String.format("%03d", new Random().nextInt(100)),
                "LINK_DOWN",
                "CRITICAL",
                DateUtil.now()
        );
        NetworkEventProcessor.publishEvent(event);
    }
}
