package com.amdocs.telecom.service;

import com.amdocs.telecom.enums.IncidentCategory;
import com.amdocs.telecom.enums.TicketPriority;
import com.amdocs.telecom.enums.TicketStatus;

import java.util.Map;

public interface ReportService {
    Map<TicketStatus, Long> getTicketsByStatus();
    Map<TicketPriority, Long> getTicketsByPriority();
    Map<String, Long> getEngineerWorkload();
    Map<String, Long> getSlaBreachAnalysis();
    double getAverageResolutionTimeHours();
    Map<String, Long> getTicketsByRegion();
    Map<IncidentCategory, Long> getTopIncidentCategories();
    Map<String, Long> getEngineerPerformance();
    Map<String, Long> getCustomersWithRepeatedIncidents();
    
    String generateFullReportText();
}
