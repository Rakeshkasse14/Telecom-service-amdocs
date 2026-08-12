package com.amdocs.telecom.model;

import com.amdocs.telecom.enums.CustomerType;

public class Customer {
    private int customerId;
    private String customerNumber;
    private String customerName;
    private String email;
    private String mobileNumber;
    private CustomerType customerType;
    private String city;
    private String status;

    public Customer() {}

    public Customer(int customerId, String customerNumber, String customerName, String email, 
                    String mobileNumber, CustomerType customerType, String city, String status) {
        this.customerId = customerId;
        this.customerNumber = customerNumber;
        this.customerName = customerName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.customerType = customerType;
        this.city = city;
        this.status = status;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public CustomerType getCustomerType() { return customerType; }
    public void setCustomerType(CustomerType customerType) { this.customerType = customerType; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + customerId +
                ", number='" + customerNumber + '\'' +
                ", name='" + customerName + '\'' +
                ", type=" + customerType +
                ", city='" + city + '\'' +
                '}';
    }
}
