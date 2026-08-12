package com.amdocs.telecom.model;

public class AuditLog {
    private int auditId;
    private String action;
    private String performedBy;
    private String timestamp;
    private String details;

    public AuditLog() {}

    public AuditLog(int auditId, String action, String performedBy, String timestamp, String details) {
        this.auditId = auditId;
        this.action = action;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
        this.details = details;
    }

    public int getAuditId() { return auditId; }
    public void setAuditId(int auditId) { this.auditId = auditId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
