package com.amdocs.telecom.service;

import com.amdocs.telecom.exception.EngineerUnavailableException;
import com.amdocs.telecom.model.NetworkEngineer;

import java.util.List;
import java.util.Optional;

public interface EngineerAssignmentService {
    Optional<NetworkEngineer> recommendBestEngineer(String specialization, String region);
    List<NetworkEngineer> getTopRecommendedEngineers(String specialization, int limit);
    boolean assignEngineerToTicketTransactional(int ticketId, int engineerId, String assignedBy) throws EngineerUnavailableException;
}
