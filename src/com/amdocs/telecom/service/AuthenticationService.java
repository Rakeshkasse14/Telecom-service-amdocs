package com.amdocs.telecom.service;

import com.amdocs.telecom.dto.UserSessionDTO;
import com.amdocs.telecom.enums.UserRole;
import com.amdocs.telecom.exception.AuthenticationException;
import com.amdocs.telecom.security.CaptchaGenerator;

public interface AuthenticationService {
    CaptchaGenerator.CaptchaChallenge generateCaptcha();
    String sendOTP(String username);
    UserSessionDTO login(String username, String password, String captchaInput, String captchaAnswer, String otpInput, UserRole role) throws AuthenticationException;
    void resetPassword(String username, String newPassword) throws AuthenticationException;
}
