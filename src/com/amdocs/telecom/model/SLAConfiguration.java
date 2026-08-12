package com.amdocs.telecom.model;

import com.amdocs.telecom.enums.TicketPriority;

public class SLAConfiguration {
    private int slaId;
    private TicketPriority priority;
    private int responseSlaMinutes;
    private int resolutionSlaHours;

    public SLAConfiguration() {}

    public SLAConfiguration(int slaId, TicketPriority priority, int responseSlaMinutes, int resolutionSlaHours) {
        this.slaId = slaId;
        this.priority = priority;
        this.responseSlaMinutes = responseSlaMinutes;
        this.resolutionSlaHours = resolutionSlaHours;
    }

    public int getSlaId() { return slaId; }
    public void setSlaId(int slaId) { this.slaId = slaId; }

    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }

    public int getResponseSlaMinutes() { return responseSlaMinutes; }
    public void setResponseSlaMinutes(int responseSlaMinutes) { this.responseSlaMinutes = responseSlaMinutes; }

    public int getResolutionSlaHours() { return resolutionSlaHours; }
    public void setResolutionSlaHours(int resolutionSlaHours) { this.resolutionSlaHours = resolutionSlaHours; }
}
