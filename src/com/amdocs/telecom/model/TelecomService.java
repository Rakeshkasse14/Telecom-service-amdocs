package com.amdocs.telecom.model;

import com.amdocs.telecom.enums.ServiceType;

public class TelecomService {
    private int serviceId;
    private String serviceCode;
    private String serviceName;
    private ServiceType serviceType;
    private int customerId;
    private String activationDate;
    private String serviceStatus;

    public TelecomService() {}

    public TelecomService(int serviceId, String serviceCode, String serviceName, ServiceType serviceType,
                          int customerId, String activationDate, String serviceStatus) {
        this.serviceId = serviceId;
        this.serviceCode = serviceCode;
        this.serviceName = serviceName;
        this.serviceType = serviceType;
        this.customerId = customerId;
        this.activationDate = activationDate;
        this.serviceStatus = serviceStatus;
    }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getServiceCode() { return serviceCode; }
    public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public ServiceType getServiceType() { return serviceType; }
    public void setServiceType(ServiceType serviceType) { this.serviceType = serviceType; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getActivationDate() { return activationDate; }
    public void setActivationDate(String activationDate) { this.activationDate = activationDate; }

    public String getServiceStatus() { return serviceStatus; }
    public void setServiceStatus(String serviceStatus) { this.serviceStatus = serviceStatus; }
}
