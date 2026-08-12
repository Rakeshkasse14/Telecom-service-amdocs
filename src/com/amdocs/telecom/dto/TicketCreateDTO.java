package com.amdocs.telecom.dto;

import com.amdocs.telecom.enums.IncidentCategory;
import com.amdocs.telecom.enums.TicketPriority;

public class TicketCreateDTO {
    private int customerId;
    private int serviceId;
    private IncidentCategory category;
    private String description;
    private TicketPriority priority;
    private String severity;

    public TicketCreateDTO(int customerId, int serviceId, IncidentCategory category,
                           String description, TicketPriority priority, String severity) {
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.category = category;
        this.description = description;
        this.priority = priority;
        this.severity = severity;
    }

    public int getCustomerId() { return customerId; }
    public int getServiceId() { return serviceId; }
    public IncidentCategory getCategory() { return category; }
    public String getDescription() { return description; }
    public TicketPriority getPriority() { return priority; }
    public String getSeverity() { return severity; }
}
