package com.amdocs.telecom.model;

public class NetworkEngineer {
    private int engineerId;
    private String employeeCode;
    private String engineerName;
    private String specialization;
    private String region;
    private int experienceYears;
    private boolean availability;
    private int activeTicketCount;

    public NetworkEngineer() {}

    public NetworkEngineer(int engineerId, String employeeCode, String engineerName,
                           String specialization, String region, int experienceYears,
                           boolean availability, int activeTicketCount) {
        this.engineerId = engineerId;
        this.employeeCode = employeeCode;
        this.engineerName = engineerName;
        this.specialization = specialization;
        this.region = region;
        this.experienceYears = experienceYears;
        this.availability = availability;
        this.activeTicketCount = activeTicketCount;
    }

    public int getEngineerId() { return engineerId; }
    public void setEngineerId(int engineerId) { this.engineerId = engineerId; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getEngineerName() { return engineerName; }
    public void setEngineerName(String engineerName) { this.engineerName = engineerName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public boolean isAvailability() { return availability; }
    public void setAvailability(boolean availability) { this.availability = availability; }

    public int getActiveTicketCount() { return activeTicketCount; }
    public void setActiveTicketCount(int activeTicketCount) { this.activeTicketCount = activeTicketCount; }

    @Override
    public String toString() {
        return String.format("%s (%s) | Skill: %s | Region: %s | Exp: %dyrs | Active: %d | Available: %s",
                employeeCode, engineerName, specialization, region, experienceYears, activeTicketCount, availability);
    }
}
