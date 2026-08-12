package com.amdocs.telecom.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordUtil {

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPassword = salt + password;
            byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String inputPassword, String storedHash, String salt) {
        String computedHash = hashPassword(inputPassword, salt);
        return computedHash.equalsIgnoreCase(storedHash);
    }
}
