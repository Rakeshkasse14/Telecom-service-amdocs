package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.AuditLog;
import java.util.List;

public interface AuditDAO {
    boolean logAction(String action, String performedBy, String details);
    List<AuditLog> findAll();
}
