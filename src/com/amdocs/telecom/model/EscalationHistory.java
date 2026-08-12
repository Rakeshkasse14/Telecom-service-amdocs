package com.amdocs.telecom.model;

public class EscalationHistory {
    private int escalationId;
    private int ticketId;
    private String fromLevel;
    private String toLevel;
    private String reason;
    private String escalationDate;
    private String escalatedBy;

    public EscalationHistory() {}

    public EscalationHistory(int escalationId, int ticketId, String fromLevel,
                             String toLevel, String reason, String escalationDate, String escalatedBy) {
        this.escalationId = escalationId;
        this.ticketId = ticketId;
        this.fromLevel = fromLevel;
        this.toLevel = toLevel;
        this.reason = reason;
        this.escalationDate = escalationDate;
        this.escalatedBy = escalatedBy;
    }

    public int getEscalationId() { return escalationId; }
    public void setEscalationId(int escalationId) { this.escalationId = escalationId; }

    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public String getFromLevel() { return fromLevel; }
    public void setFromLevel(String fromLevel) { this.fromLevel = fromLevel; }

    public String getToLevel() { return toLevel; }
    public void setToLevel(String toLevel) { this.toLevel = toLevel; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getEscalationDate() { return escalationDate; }
    public void setEscalationDate(String escalationDate) { this.escalationDate = escalationDate; }

    public String getEscalatedBy() { return escalatedBy; }
    public void setEscalatedBy(String escalatedBy) { this.escalatedBy = escalatedBy; }
}
