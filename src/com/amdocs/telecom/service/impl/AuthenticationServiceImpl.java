package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.dto.UserSessionDTO;
import com.amdocs.telecom.enums.UserRole;
import com.amdocs.telecom.exception.AuthenticationException;
import com.amdocs.telecom.security.CaptchaGenerator;
import com.amdocs.telecom.security.OTPService;
import com.amdocs.telecom.security.PasswordUtil;
import com.amdocs.telecom.service.AuthenticationService;
import com.amdocs.telecom.util.DBConnection;
import com.amdocs.telecom.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationServiceImpl implements AuthenticationService {

    private final CaptchaGenerator captchaGenerator = CaptchaGenerator.getInstance();
    private final OTPService otpService = new OTPService();

    @Override
    public CaptchaGenerator.CaptchaChallenge generateCaptcha() {
        return captchaGenerator.generateChallenge();
    }

    @Override
    public String sendOTP(String username) {
        return otpService.generateOTP(username);
    }

    @Override
    public UserSessionDTO login(String username, String password, String captchaInput, String captchaAnswer, String otpInput, UserRole role) throws AuthenticationException {
        // 1. Verify CAPTCHA
        if (captchaInput == null || !captchaInput.trim().equalsIgnoreCase(captchaAnswer.trim())) {
            recordLoginAttempt(username, role.name(), false);
            throw new AuthenticationException("Invalid CAPTCHA solution! Please try again.");
        }

        // 2. Fetch User Credentials from Database
        String sql = "SELECT password_hash, salt, role, failed_attempts, locked FROM user_credentials WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    recordLoginAttempt(username, role.name(), false);
                    throw new AuthenticationException("User " + username + " not found!");
                }

                boolean locked = rs.getBoolean("locked");
                int failedAttempts = rs.getInt("failed_attempts");

                if (locked) {
                    recordLoginAttempt(username, role.name(), false);
                    throw new AuthenticationException("Account " + username + " is LOCKED due to 3 consecutive failed login attempts! Contact Admin.");
                }

                String dbRole = rs.getString("role");
                if (!dbRole.equalsIgnoreCase(role.name())) {
                    recordLoginAttempt(username, role.name(), false);
                    throw new AuthenticationException("Unauthorized Role! User " + username + " is registered as " + dbRole + ", not " + role);
                }

                String salt = rs.getString("salt");
                String storedHash = rs.getString("password_hash");

                // 3. Verify Password
                if (!PasswordUtil.verifyPassword(password, storedHash, salt)) {
                    int newFailedCount = failedAttempts + 1;
                    updateFailedAttempts(username, newFailedCount, newFailedCount >= 3);
                    recordLoginAttempt(username, role.name(), false);
                    if (newFailedCount >= 3) {
                        throw new AuthenticationException("Invalid password! Account has now been LOCKED after 3 failed attempts.");
                    }
                    throw new AuthenticationException("Invalid password! Attempt " + newFailedCount + " of 3.");
                }

                // 4. Verify OTP
                if (otpInput != null && !otpInput.isEmpty()) {
                    if (!otpService.validateOTP(username, otpInput)) {
                        recordLoginAttempt(username, role.name(), false);
                        throw new AuthenticationException("Invalid 6-Digit OTP Code!");
                    }
                }

                // Reset failed attempts on success
                updateFailedAttempts(username, 0, false);
                recordLoginAttempt(username, role.name(), true);

                int entityId = resolveEntityId(username, role);
                return new UserSessionDTO(username, role, DateUtil.now(), entityId);
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Database error during authentication: " + e.getMessage(), e);
        }
    }

    @Override
    public void resetPassword(String username, String newPassword) throws AuthenticationException {
        String salt = "telecomSalt123";
        String newHash = PasswordUtil.hashPassword(newPassword, salt);
        String sql = "UPDATE user_credentials SET password_hash = ?, failed_attempts = 0, locked = 0 WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setString(2, username);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new AuthenticationException("User " + username + " does not exist.");
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Error resetting password: " + e.getMessage(), e);
        }
    }

    private void updateFailedAttempts(String username, int attempts, boolean lock) {
        String sql = "UPDATE user_credentials SET failed_attempts = ?, locked = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attempts);
            ps.setBoolean(2, lock);
            ps.setString(3, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void recordLoginAttempt(String username, String role, boolean success) {
        String sql = "INSERT INTO login_history (username, user_role, login_time, success, ip_address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, role);
            ps.setString(3, DateUtil.now());
            ps.setBoolean(4, success);
            ps.setString(5, "127.0.0.1");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int resolveEntityId(String username, UserRole role) {
        if (role == UserRole.CUSTOMER) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT customer_id FROM customers WHERE customer_number = ?")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            } catch (SQLException ignored) {}
        } else if (role == UserRole.NETWORK_ENGINEER) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT engineer_id FROM network_engineers WHERE employee_code = ?")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            } catch (SQLException ignored) {}
        }
        return 1;
    }
}
