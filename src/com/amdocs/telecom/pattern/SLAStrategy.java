package com.amdocs.telecom.pattern;

import com.amdocs.telecom.enums.TicketPriority;

public interface SLAStrategy {
    int getResponseSlaMinutes(TicketPriority priority);
    int getResolutionSlaHours(TicketPriority priority);
}
