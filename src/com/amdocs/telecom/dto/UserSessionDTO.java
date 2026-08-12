package com.amdocs.telecom.dto;

import com.amdocs.telecom.enums.UserRole;

public class UserSessionDTO {
    private String username;
    private UserRole role;
    private String loginTime;
    private int associatedEntityId; // customerId or engineerId

    public UserSessionDTO(String username, UserRole role, String loginTime, int associatedEntityId) {
        this.username = username;
        this.role = role;
        this.loginTime = loginTime;
        this.associatedEntityId = associatedEntityId;
    }

    public String getUsername() { return username; }
    public UserRole getRole() { return role; }
    public String getLoginTime() { return loginTime; }
    public int getAssociatedEntityId() { return associatedEntityId; }
}
