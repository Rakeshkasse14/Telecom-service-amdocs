package com.amdocs.telecom.model;

import com.amdocs.telecom.enums.TicketStatus;

public class TicketStatusHistory {
    private int historyId;
    private int ticketId;
    private TicketStatus oldStatus;
    private TicketStatus newStatus;
    private String changedBy;
    private String changedDate;
    private String remarks;

    public TicketStatusHistory() {}

    public TicketStatusHistory(int historyId, int ticketId, TicketStatus oldStatus,
                               TicketStatus newStatus, String changedBy, String changedDate, String remarks) {
        this.historyId = historyId;
        this.ticketId = ticketId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changedDate = changedDate;
        this.remarks = remarks;
    }

    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public TicketStatus getOldStatus() { return oldStatus; }
    public void setOldStatus(TicketStatus oldStatus) { this.oldStatus = oldStatus; }

    public TicketStatus getNewStatus() { return newStatus; }
    public void setNewStatus(TicketStatus newStatus) { this.newStatus = newStatus; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getChangedDate() { return changedDate; }
    public void setChangedDate(String changedDate) { this.changedDate = changedDate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
