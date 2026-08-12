package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.NetworkEngineer;
import java.util.List;

public interface EngineerDAO {
    NetworkEngineer findById(int engineerId);
    NetworkEngineer findByEmployeeCode(String employeeCode);
    List<NetworkEngineer> findAll();
    boolean updateWorkloadAndAvailability(int engineerId, int activeCount, boolean available);
}
