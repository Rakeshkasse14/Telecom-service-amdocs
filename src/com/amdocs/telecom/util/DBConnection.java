package com.amdocs.telecom.util;

import com.amdocs.telecom.security.PasswordUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String DB_URL = "jdbc:sqlite:tsatms.db";
    private static Connection connection;

    private DBConnection() {}

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC Driver not found on classpath", e);
            }
            connection = DriverManager.getConnection(DB_URL);
            // Enable Foreign Keys in SQLite
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Customers Table
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (" +
                    "customer_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "customer_number TEXT UNIQUE NOT NULL, " +
                    "customer_name TEXT NOT NULL, " +
                    "email TEXT NOT NULL, " +
                    "mobile_number TEXT NOT NULL, " +
                    "customer_type TEXT NOT NULL, " +
                    "city TEXT NOT NULL, " +
                    "status TEXT DEFAULT 'ACTIVE', " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ");");

            // 2. Telecom Services Table
            stmt.execute("CREATE TABLE IF NOT EXISTS telecom_services (" +
                    "service_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "service_code TEXT UNIQUE NOT NULL, " +
                    "service_name TEXT NOT NULL, " +
                    "service_type TEXT NOT NULL, " +
                    "customer_id INTEGER NOT NULL, " +
                    "activation_date TEXT NOT NULL, " +
                    "service_status TEXT DEFAULT 'ACTIVE', " +
                    "FOREIGN KEY(customer_id) REFERENCES customers(customer_id)" +
                    ");");

            // 3. Network Engineers Table
            stmt.execute("CREATE TABLE IF NOT EXISTS network_engineers (" +
                    "engineer_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "employee_code TEXT UNIQUE NOT NULL, " +
                    "engineer_name TEXT NOT NULL, " +
                    "specialization TEXT NOT NULL, " +
                    "region TEXT NOT NULL, " +
                    "experience_years INTEGER NOT NULL, " +
                    "availability BOOLEAN DEFAULT 1, " +
                    "active_ticket_count INTEGER DEFAULT 0" +
                    ");");

            // 4. SLA Configuration Table
            stmt.execute("CREATE TABLE IF NOT EXISTS sla_configs (" +
                    "sla_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "priority TEXT UNIQUE NOT NULL, " +
                    "response_sla_minutes INTEGER NOT NULL, " +
                    "resolution_sla_hours INTEGER NOT NULL" +
                    ");");

            // 5. Trouble Tickets Table
            stmt.execute("CREATE TABLE IF NOT EXISTS trouble_tickets (" +
                    "ticket_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ticket_number TEXT UNIQUE NOT NULL, " +
                    "customer_id INTEGER NOT NULL, " +
                    "service_id INTEGER NOT NULL, " +
                    "category TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "priority TEXT NOT NULL, " +
                    "severity TEXT NOT NULL, " +
                    "created_date TEXT NOT NULL, " +
                    "assigned_engineer_id INTEGER, " +
                    "status TEXT NOT NULL, " +
                    "sla_deadline TEXT NOT NULL, " +
                    "resolution_date TEXT, " +
                    "sla_status TEXT NOT NULL, " +
                    "root_cause TEXT, " +
                    "resolution_details TEXT, " +
                    "resolution_code TEXT, " +
                    "FOREIGN KEY(customer_id) REFERENCES customers(customer_id), " +
                    "FOREIGN KEY(service_id) REFERENCES telecom_services(service_id), " +
                    "FOREIGN KEY(assigned_engineer_id) REFERENCES network_engineers(engineer_id)" +
                    ");");

            // 6. Ticket Status History Table
            stmt.execute("CREATE TABLE IF NOT EXISTS ticket_status_history (" +
                    "history_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ticket_id INTEGER NOT NULL, " +
                    "old_status TEXT, " +
                    "new_status TEXT NOT NULL, " +
                    "changed_by TEXT NOT NULL, " +
                    "changed_date TEXT NOT NULL, " +
                    "remarks TEXT, " +
                    "FOREIGN KEY(ticket_id) REFERENCES trouble_tickets(ticket_id)" +
                    ");");

            // 7. Escalation History Table
            stmt.execute("CREATE TABLE IF NOT EXISTS escalation_history (" +
                    "escalation_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ticket_id INTEGER NOT NULL, " +
                    "from_level TEXT NOT NULL, " +
                    "to_level TEXT NOT NULL, " +
                    "reason TEXT NOT NULL, " +
                    "escalation_date TEXT NOT NULL, " +
                    "escalated_by TEXT NOT NULL, " +
                    "FOREIGN KEY(ticket_id) REFERENCES trouble_tickets(ticket_id)" +
                    ");");

            // 8. Network Events Table
            stmt.execute("CREATE TABLE IF NOT EXISTS network_events (" +
                    "event_id TEXT PRIMARY KEY, " +
                    "network_node TEXT NOT NULL, " +
                    "event_type TEXT NOT NULL, " +
                    "severity TEXT NOT NULL, " +
                    "event_time TEXT NOT NULL" +
                    ");");

            // 9. Notifications Table
            stmt.execute("CREATE TABLE IF NOT EXISTS notifications (" +
                    "notification_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "recipient_id TEXT NOT NULL, " +
                    "message TEXT NOT NULL, " +
                    "notification_type TEXT NOT NULL, " +
                    "created_date TEXT NOT NULL, " +
                    "read_status BOOLEAN DEFAULT 0" +
                    ");");

            // 10. Feedback Table
            stmt.execute("CREATE TABLE IF NOT EXISTS feedback (" +
                    "feedback_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ticket_id INTEGER NOT NULL, " +
                    "customer_id INTEGER NOT NULL, " +
                    "rating INTEGER NOT NULL, " +
                    "comments TEXT, " +
                    "feedback_date TEXT NOT NULL, " +
                    "FOREIGN KEY(ticket_id) REFERENCES trouble_tickets(ticket_id)" +
                    ");");

            // 11. Audit Log Table
            stmt.execute("CREATE TABLE IF NOT EXISTS audit_logs (" +
                    "audit_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "action TEXT NOT NULL, " +
                    "performed_by TEXT NOT NULL, " +
                    "timestamp TEXT NOT NULL, " +
                    "details TEXT" +
                    ");");

            // 12. Login History & Credentials Table
            stmt.execute("CREATE TABLE IF NOT EXISTS login_history (" +
                    "history_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT NOT NULL, " +
                    "user_role TEXT NOT NULL, " +
                    "login_time TEXT NOT NULL, " +
                    "success BOOLEAN NOT NULL, " +
                    "ip_address TEXT NOT NULL" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS user_credentials (" +
                    "username TEXT PRIMARY KEY, " +
                    "password_hash TEXT NOT NULL, " +
                    "salt TEXT NOT NULL, " +
                    "role TEXT NOT NULL, " +
                    "failed_attempts INTEGER DEFAULT 0, " +
                    "locked BOOLEAN DEFAULT 0" +
                    ");");

            // Create indices for fast query processing
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_ticket_status ON trouble_tickets(status);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_ticket_customer ON trouble_tickets(customer_id);");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_engineer_region ON network_engineers(region, specialization);");

            // Seed initial data if tables are empty
            seedInitialData(conn);

        } catch (SQLException e) {
            System.err.println("Database Initialization Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void seedInitialData(Connection conn) throws SQLException {
        // Check if customers table is empty
        try (Statement checkStmt = conn.createStatement();
             ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) FROM customers")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Data already seeded
            }
        }

        System.out.println(">> Seeding TSATMS Initial Sample Data...");

        // Insert Default Users for Login
        String salt = "telecomSalt123";
        String defaultPassHash = PasswordUtil.hashPassword("Password@123", salt);

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO user_credentials (username, password_hash, salt, role) VALUES (?, ?, ?, ?)")) {
            
            // Customer User
            ps.setString(1, "CUST100245");
            ps.setString(2, defaultPassHash);
            ps.setString(3, salt);
            ps.setString(4, "CUSTOMER");
            ps.executeUpdate();

            // Service Desk User
            ps.setString(1, "ADMIN01");
            ps.setString(2, defaultPassHash);
            ps.setString(3, salt);
            ps.setString(4, "SERVICE_DESK");
            ps.executeUpdate();

            // Engineer User
            ps.setString(1, "ENG1008");
            ps.setString(2, defaultPassHash);
            ps.setString(3, salt);
            ps.setString(4, "NETWORK_ENGINEER");
            ps.executeUpdate();

            // Manager User
            ps.setString(1, "MGR01");
            ps.setString(2, defaultPassHash);
            ps.setString(3, salt);
            ps.setString(4, "NETWORK_MANAGER");
            ps.executeUpdate();
        }

        // Insert Customers
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO customers (customer_id, customer_number, customer_name, email, mobile_number, customer_type, city, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, 101); ps.setString(2, "CUST100245"); ps.setString(3, "Acme Corp"); ps.setString(4, "admin@acme.com"); ps.setString(5, "+91-9876543210"); ps.setString(6, "ENTERPRISE"); ps.setString(7, "Mumbai"); ps.setString(8, "ACTIVE"); ps.executeUpdate();
            ps.setInt(1, 102); ps.setString(2, "CUST100246"); ps.setString(3, "Rahul Sharma"); ps.setString(4, "rahul@gmail.com"); ps.setString(5, "+91-9812345678"); ps.setString(6, "CONSUMER"); ps.setString(7, "Delhi"); ps.setString(8, "ACTIVE"); ps.executeUpdate();
            ps.setInt(1, 103); ps.setString(2, "CUST100247"); ps.setString(3, "Global Tech Solutions"); ps.setString(4, "it@globaltech.com"); ps.setString(5, "+91-9823456789"); ps.setString(6, "SME"); ps.setString(7, "Bengaluru"); ps.setString(8, "ACTIVE"); ps.executeUpdate();
        }

        // Insert Telecom Services
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO telecom_services (service_id, service_code, service_name, service_type, customer_id, activation_date, service_status) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, 501); ps.setString(2, "SRV-ENT-001"); ps.setString(3, "Enterprise Fiber Link 1Gbps"); ps.setString(4, "ENTERPRISE_CONNECTIVITY"); ps.setInt(5, 101); ps.setString(6, "2025-01-10"); ps.setString(7, "ACTIVE"); ps.executeUpdate();
            ps.setInt(1, 502); ps.setString(2, "SRV-MOB-002"); ps.setString(3, "5G Unlimited Postpaid"); ps.setString(4, "MOBILE"); ps.setInt(5, 102); ps.setString(6, "2025-03-15"); ps.setString(7, "ACTIVE"); ps.executeUpdate();
            ps.setInt(1, 503); ps.setString(2, "SRV-VPN-003"); ps.setString(3, "MPLS Secure VPN"); ps.setString(4, "VPN"); ps.setInt(5, 103); ps.setString(6, "2025-02-20"); ps.setString(7, "ACTIVE"); ps.executeUpdate();
        }

        // Insert Network Engineers (Matching examples in PDF Page 5 & 6)
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO network_engineers (engineer_id, employee_code, engineer_name, specialization, region, experience_years, availability, active_ticket_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, 1); ps.setString(2, "ENG1008"); ps.setString(3, "Amit Verma"); ps.setString(4, "Core Network"); ps.setString(5, "West"); ps.setInt(6, 9); ps.setBoolean(7, true); ps.setInt(8, 2); ps.executeUpdate();
            ps.setInt(1, 2); ps.setString(2, "ENG1015"); ps.setString(3, "Priya Nair"); ps.setString(4, "RAN"); ps.setString(5, "North"); ps.setInt(6, 7); ps.setBoolean(7, true); ps.setInt(8, 4); ps.executeUpdate();
            ps.setInt(1, 3); ps.setString(2, "ENG1021"); ps.setString(3, "Suresh Kumar"); ps.setString(4, "Broadband"); ps.setString(5, "South"); ps.setInt(6, 6); ps.setBoolean(7, true); ps.setInt(8, 1); ps.executeUpdate();
            ps.setInt(1, 4); ps.setString(2, "ENG1030"); ps.setString(3, "Vikram Singh"); ps.setString(4, "IP Network"); ps.setString(5, "West"); ps.setInt(6, 10); ps.setBoolean(7, true); ps.setInt(8, 3); ps.executeUpdate();
        }

        // Insert SLA Configurations (Matching PDF Page 6)
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO sla_configs (priority, response_sla_minutes, resolution_sla_hours) VALUES (?, ?, ?)")) {
            ps.setString(1, "CRITICAL"); ps.setInt(2, 15); ps.setInt(3, 2); ps.executeUpdate();
            ps.setString(1, "HIGH"); ps.setInt(2, 30); ps.setInt(3, 4); ps.executeUpdate();
            ps.setString(1, "MEDIUM"); ps.setInt(2, 120); ps.setInt(3, 12); ps.executeUpdate();
            ps.setString(1, "LOW"); ps.setInt(2, 480); ps.setInt(3, 48); ps.executeUpdate();
        }

        // Insert Initial Sample Trouble Tickets
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO trouble_tickets (ticket_id, ticket_number, customer_id, service_id, category, description, priority, severity, created_date, assigned_engineer_id, status, sla_deadline, sla_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, 1001); ps.setString(2, "TT-2026-004521"); ps.setInt(3, 101); ps.setInt(4, 501);
            ps.setString(5, "NETWORK_OUTAGE"); ps.setString(6, "Enterprise link down due to fiber cut near West Node.");
            ps.setString(7, "CRITICAL"); ps.setString(8, "CRITICAL"); ps.setString(9, "2026-08-11 18:00:00");
            ps.setInt(10, 1); ps.setString(11, "IN_PROGRESS"); ps.setString(12, "2026-08-11 20:00:00"); ps.setString(13, "WITHIN_SLA");
            ps.executeUpdate();

            ps.setInt(1, 1002); ps.setString(2, "TT-2026-004522"); ps.setInt(3, 102); ps.setInt(4, 502);
            ps.setString(5, "SLOW_DATA"); ps.setString(6, "5G speed dropping below 1 Mbps in Delhi North zone.");
            ps.setString(7, "MEDIUM"); ps.setString(8, "MAJOR"); ps.setString(9, "2026-08-11 15:30:00");
            ps.setInt(10, 2); ps.setString(11, "OPEN"); ps.setString(12, "2026-08-12 03:30:00"); ps.setString(13, "WITHIN_SLA");
            ps.executeUpdate();
        }

        System.out.println(">> Sample Data Seeded Successfully!");
    }
}
