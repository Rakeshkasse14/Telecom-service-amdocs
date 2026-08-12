package com.amdocs.telecom.enums;

public enum TicketPriority {
    LOW(8, 48),       // Response: 8h, Resolution: 48h
    MEDIUM(2, 12),    // Response: 2h, Resolution: 12h
    HIGH(1, 4),       // Response: 30m (0.5h/1h), Resolution: 4h
    CRITICAL(1, 2);   // Response: 15m, Resolution: 2h

    private final int responseSlaHours;
    private final int resolutionSlaHours;

    TicketPriority(int responseSlaHours, int resolutionSlaHours) {
        this.responseSlaHours = responseSlaHours;
        this.resolutionSlaHours = resolutionSlaHours;
    }

    public int getResponseSlaHours() {
        return responseSlaHours;
    }

    public int getResolutionSlaHours() {
        return resolutionSlaHours;
    }
}
