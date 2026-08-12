package com.amdocs.telecom.pattern;

import com.amdocs.telecom.enums.TicketPriority;

public class DefaultSLAStrategy implements SLAStrategy {

    @Override
    public int getResponseSlaMinutes(TicketPriority priority) {
        switch (priority) {
            case CRITICAL: return 15;
            case HIGH: return 30;
            case MEDIUM: return 120;
            case LOW: return 480;
            default: return 480;
        }
    }

    @Override
    public int getResolutionSlaHours(TicketPriority priority) {
        switch (priority) {
            case CRITICAL: return 2;
            case HIGH: return 4;
            case MEDIUM: return 12;
            case LOW: return 48;
            default: return 48;
        }
    }
}
