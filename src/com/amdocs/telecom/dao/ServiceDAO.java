package com.amdocs.telecom.dao;

import com.amdocs.telecom.model.TelecomService;
import java.util.List;

public interface ServiceDAO {
    TelecomService findById(int serviceId);
    List<TelecomService> findByCustomerId(int customerId);
    List<TelecomService> findAll();
}
