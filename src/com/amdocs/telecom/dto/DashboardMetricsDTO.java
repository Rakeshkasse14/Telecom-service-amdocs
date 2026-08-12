package com.amdocs.telecom.dto;

public class DashboardMetricsDTO {
    private int totalOpenTickets;
    private int criticalIncidents;
    private int slaAtRisk;
    private int slaBreached;
    private int resolvedToday;
    private double averageResolutionTimeHours;

    public DashboardMetricsDTO(int totalOpenTickets, int criticalIncidents, int slaAtRisk,
                               int slaBreached, int resolvedToday, double averageResolutionTimeHours) {
        this.totalOpenTickets = totalOpenTickets;
        this.criticalIncidents = criticalIncidents;
        this.slaAtRisk = slaAtRisk;
        this.slaBreached = slaBreached;
        this.resolvedToday = resolvedToday;
        this.averageResolutionTimeHours = averageResolutionTimeHours;
    }

    public int getTotalOpenTickets() { return totalOpenTickets; }
    public int getCriticalIncidents() { return criticalIncidents; }
    public int getSlaAtRisk() { return slaAtRisk; }
    public int getSlaBreached() { return slaBreached; }
    public int getResolvedToday() { return resolvedToday; }
    public double getAverageResolutionTimeHours() { return averageResolutionTimeHours; }

    public void displayNetworkManagerDashboard() {
        System.out.println("====================================================");
        System.out.println("            NETWORK MANAGER DASHBOARD               ");
        System.out.println("====================================================");
        System.out.printf("  Total Open Tickets       : %d\n", totalOpenTickets);
        System.out.printf("  Critical Incidents       : %d\n", criticalIncidents);
        System.out.printf("  SLA At Risk              : %d\n", slaAtRisk);
        System.out.printf("  SLA Breached             : %d\n", slaBreached);
        System.out.printf("  Resolved Today           : %d\n", resolvedToday);
        System.out.printf("  Average Resolution Time  : %.1f Hours\n", averageResolutionTimeHours);
        System.out.println("====================================================");
    }
}
