package com.hotel.models;

public class ServiceUsageReport {
    private String serviceName;
    private int usageCount;
    private double totalRevenue;

    public ServiceUsageReport(String serviceName, int usageCount, double totalRevenue) {
        this.serviceName = serviceName;
        this.usageCount = usageCount;
        this.totalRevenue = totalRevenue;
    }

    // Getters and Setters
    public String getServiceName() { return serviceName; }
    public int getUsageCount() { return usageCount; }
    public double getTotalRevenue() { return totalRevenue; }

    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
}
