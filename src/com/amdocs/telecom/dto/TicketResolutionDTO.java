package com.amdocs.telecom.dto;

import com.amdocs.telecom.enums.ResolutionCode;

public class TicketResolutionDTO {
    private int ticketId;
    private String rootCause;
    private String resolutionDetails;
    private ResolutionCode resolutionCode;
    private String engineerCode;

    public TicketResolutionDTO(int ticketId, String rootCause, String resolutionDetails, ResolutionCode resolutionCode, String engineerCode) {
        this.ticketId = ticketId;
        this.rootCause = rootCause;
        this.resolutionDetails = resolutionDetails;
        this.resolutionCode = resolutionCode;
        this.engineerCode = engineerCode;
    }

    public int getTicketId() { return ticketId; }
    public String getRootCause() { return rootCause; }
    public String getResolutionDetails() { return resolutionDetails; }
    public ResolutionCode getResolutionCode() { return resolutionCode; }
    public String getEngineerCode() { return engineerCode; }
}
