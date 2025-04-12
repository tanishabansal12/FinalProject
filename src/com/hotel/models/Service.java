package com.hotel.models;

public class Service {
    private String serviceName;
    private double price;  // New field for service price

    // Constructor with service name and price
    public Service(String serviceName, double price) {
        this.serviceName = serviceName;
        this.price = price;
    }

    // Constructor with only service name (assume price is 0.0 if not provided)
    public Service(String serviceName) {
        this(serviceName, 0.0);  // Default price as 0.0
    }

    // Getters and Setters
    public String getServiceName() { return serviceName; }
    public double getPrice() { return price; }

    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public void setPrice(double price) { this.price = price; }
}
