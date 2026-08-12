package com.amdocs.telecom.service.impl;

import com.amdocs.telecom.dao.EngineerDAO;
import com.amdocs.telecom.dao.TicketDAO;
import com.amdocs.telecom.dao.impl.EngineerDAOImpl;
import com.amdocs.telecom.dao.impl.TicketDAOImpl;
import com.amdocs.telecom.exception.EngineerUnavailableException;
import com.amdocs.telecom.model.NetworkEngineer;
import com.amdocs.telecom.service.EngineerAssignmentService;
import com.amdocs.telecom.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EngineerAssignmentServiceImpl implements EngineerAssignmentService {

    private final EngineerDAO engineerDAO = new EngineerDAOImpl();
    private final TicketDAO ticketDAO = new TicketDAOImpl();

    /**
     * Recommends the single best Network Engineer using Java 8 Stream API + Lambda + Comparator + Optional.
     * Recommendation logic:
     * 1. Filter available engineers
     * 2. Filter matching specialization or region
     * 3. Sort by workload (ascending) then experience (descending)
     * 4. Return Optional<NetworkEngineer>
     */
    @Override
    public Optional<NetworkEngineer> recommendBestEngineer(String specialization, String region) {
        List<NetworkEngineer> allEngineers = engineerDAO.findAll();

        return allEngineers.stream()
                .filter(NetworkEngineer::isAvailability) // Lambda / Method reference
                .filter(e -> e.getSpecialization().equalsIgnoreCase(specialization) || e.getRegion().equalsIgnoreCase(region))
                .min(Comparator.comparingInt(NetworkEngineer::getActiveTicketCount)
                        .thenComparing(Comparator.comparingInt(NetworkEngineer::getExperienceYears).reversed()));
    }

    /**
     * Implements PDF Page 11 Requirement:
     * "Find the three engineers with the lowest active workload who have the required specialization and are currently available."
     */
    @Override
    public List<NetworkEngineer> getTopRecommendedEngineers(String specialization, int limit) {
        List<NetworkEngineer> allEngineers = engineerDAO.findAll();

        return allEngineers.stream()
                .filter(NetworkEngineer::isAvailability)
                .filter(e -> e.getSpecialization().equalsIgnoreCase(specialization))
                .sorted(Comparator.comparingInt(NetworkEngineer::getActiveTicketCount)
                        .thenComparing(Comparator.comparingInt(NetworkEngineer::getExperienceYears).reversed()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Executes transactional assignment over JDBC Connection.
     * Enforces COMMIT on success and ROLLBACK on failure.
     */
    @Override
    public boolean assignEngineerToTicketTransactional(int ticketId, int engineerId, String assignedBy) throws EngineerUnavailableException {
        try (Connection conn = DBConnection.getConnection()) {
            return ticketDAO.assignEngineerTransaction(conn, ticketId, engineerId, assignedBy);
        } catch (SQLException e) {
            throw new EngineerUnavailableException("Transactional Assignment Failed & Rolled Back! Reason: " + e.getMessage());
        }
    }
}
