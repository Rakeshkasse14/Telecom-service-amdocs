package com.amdocs.telecom.model;

import com.amdocs.telecom.enums.IncidentCategory;
import com.amdocs.telecom.enums.ResolutionCode;
import com.amdocs.telecom.enums.SLAStatus;
import com.amdocs.telecom.enums.TicketPriority;
import com.amdocs.telecom.enums.TicketStatus;

public class TroubleTicket implements Comparable<TroubleTicket> {
    private int ticketId;
    private String ticketNumber;
    private int customerId;
    private int serviceId;
    private IncidentCategory category;
    private String description;
    private TicketPriority priority;
    private String severity;
    private String createdDate;
    private Integer assignedEngineerId;
    private TicketStatus status;
    private String slaDeadline;
    private String resolutionDate;
    private SLAStatus slaStatus;
    private String rootCause;
    private String resolutionDetails;
    private ResolutionCode resolutionCode;

    public TroubleTicket() {}

    public TroubleTicket(int ticketId, String ticketNumber, int customerId, int serviceId,
                         IncidentCategory category, String description, TicketPriority priority,
                         String severity, String createdDate, Integer assignedEngineerId,
                         TicketStatus status, String slaDeadline, String resolutionDate,
                         SLAStatus slaStatus) {
        this.ticketId = ticketId;
        this.ticketNumber = ticketNumber;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.category = category;
        this.description = description;
        this.priority = priority;
        this.severity = severity;
        this.createdDate = createdDate;
        this.assignedEngineerId = assignedEngineerId;
        this.status = status;
        this.slaDeadline = slaDeadline;
        this.resolutionDate = resolutionDate;
        this.slaStatus = slaStatus;
    }

    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public IncidentCategory getCategory() { return category; }
    public void setCategory(IncidentCategory category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public Integer getAssignedEngineerId() { return assignedEngineerId; }
    public void setAssignedEngineerId(Integer assignedEngineerId) { this.assignedEngineerId = assignedEngineerId; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public String getSlaDeadline() { return slaDeadline; }
    public void setSlaDeadline(String slaDeadline) { this.slaDeadline = slaDeadline; }

    public String getResolutionDate() { return resolutionDate; }
    public void setResolutionDate(String resolutionDate) { this.resolutionDate = resolutionDate; }

    public SLAStatus getSlaStatus() { return slaStatus; }
    public void setSlaStatus(SLAStatus slaStatus) { this.slaStatus = slaStatus; }

    public String getRootCause() { return rootCause; }
    public void setRootCause(String rootCause) { this.rootCause = rootCause; }

    public String getResolutionDetails() { return resolutionDetails; }
    public void setResolutionDetails(String resolutionDetails) { this.resolutionDetails = resolutionDetails; }

    public ResolutionCode getResolutionCode() { return resolutionCode; }
    public void setResolutionCode(ResolutionCode resolutionCode) { this.resolutionCode = resolutionCode; }

    // Natural ordering for PriorityQueue: CRITICAL > HIGH > MEDIUM > LOW
    @Override
    public int compareTo(TroubleTicket o) {
        if (this.priority == o.priority) {
            return Integer.compare(this.ticketId, o.ticketId);
        }
        return Integer.compare(o.priority.ordinal(), this.priority.ordinal());
    }

    @Override
    public String toString() {
        return String.format("[%s] Ticket #%s | Priority: %s | Status: %s | SLA: %s",
                ticketId, ticketNumber, priority, status, slaStatus);
    }
}
