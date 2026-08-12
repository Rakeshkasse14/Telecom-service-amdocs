package com.amdocs.telecom.security;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class OTPService {
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateOTP(String identifier) {
        int otp = 100000 + random.nextInt(900000);
        String otpStr = String.valueOf(otp);
        otpStore.put(identifier, otpStr);
        return otpStr;
    }

    public boolean validateOTP(String identifier, String userOTP) {
        String storedOTP = otpStore.get(identifier);
        if (storedOTP != null && storedOTP.equals(userOTP)) {
            otpStore.remove(identifier);
            return true;
        }
        return false;
    }
}
