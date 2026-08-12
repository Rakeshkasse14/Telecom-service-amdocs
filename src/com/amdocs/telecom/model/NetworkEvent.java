package com.amdocs.telecom.model;

public class NetworkEvent {
    private String eventId;
    private String networkNode;
    private String eventType;
    private String severity;
    private String eventTime;

    public NetworkEvent() {}

    public NetworkEvent(String eventId, String networkNode, String eventType, String severity, String eventTime) {
        this.eventId = eventId;
        this.networkNode = networkNode;
        this.eventType = eventType;
        this.severity = severity;
        this.eventTime = eventTime;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getNetworkNode() { return networkNode; }
    public void setNetworkNode(String networkNode) { this.networkNode = networkNode; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }

    @Override
    public String toString() {
        return String.format("Event ID: %s | Node: %s | Type: %s | Severity: %s | Time: %s",
                eventId, networkNode, eventType, severity, eventTime);
    }
}
