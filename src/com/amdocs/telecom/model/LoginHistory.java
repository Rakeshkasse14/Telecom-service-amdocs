package com.amdocs.telecom.model;

public class LoginHistory {
    private int historyId;
    private String username;
    private String userRole;
    private String loginTime;
    private boolean success;
    private String ipAddress;

    public LoginHistory() {}

    public LoginHistory(int historyId, String username, String userRole, String loginTime, boolean success, String ipAddress) {
        this.historyId = historyId;
        this.username = username;
        this.userRole = userRole;
        this.loginTime = loginTime;
        this.success = success;
        this.ipAddress = ipAddress;
    }

    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getLoginTime() { return loginTime; }
    public void setLoginTime(String loginTime) { this.loginTime = loginTime; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
