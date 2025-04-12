package com.hotel.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hotel.models.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceDAO {

    // Simulated database of services with prices (can be replaced with DB logic later)
    private static final Map<String, Double> servicePriceMap = new HashMap<>();

    static {
        servicePriceMap.put("Spa", 50.0);
        servicePriceMap.put("Dining", 30.0);
        servicePriceMap.put("Room Service", 20.0);
        servicePriceMap.put("Laundry", 15.0);
        servicePriceMap.put("Gym Access", 10.0);
    }

    public static List<Service> getServicesForGuest(String guestName) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT services FROM bills WHERE guest_name = ?";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, guestName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String serviceString = rs.getString("services");
                if (serviceString != null && !serviceString.isEmpty()) {
                    String[] serviceArray = serviceString.split(",\\s*");
                    for (String name : serviceArray) {
                        Service service = getServiceByName(name.trim());
                        if (service != null) {
                            services.add(service);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching guest services: " + e.getMessage());
        }

        return services;
    }


    public static Service getServiceByName(String name) {
        double price = servicePriceMap.getOrDefault(name, 0.0); // fallback to 0.0
        return new Service(name, price);
    }

    public static List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        for (String name : servicePriceMap.keySet()) {
            services.add(new Service(name, servicePriceMap.get(name)));
        }
        return services;
    }


}
