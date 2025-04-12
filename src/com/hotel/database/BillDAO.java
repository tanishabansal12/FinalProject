    package com.hotel.database;

    import com.hotel.models.Bill;
    import com.hotel.models.Service;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class BillDAO {

        // Method to add a new bill to the database
        public boolean addBill(Bill bill) {
            String sql = "INSERT INTO bills (guest_name, room_type, nights, services, base_charge, tax, discount, total_amount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DBConnection.getInstance();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                // Convert List<Service> to a comma-separated String (only service names for simplicity)
                String services = convertServicesToString(bill.getServices());

                stmt.setString(1, bill.getGuestName());
                stmt.setString(2, bill.getRoomType());
                stmt.setInt(3, bill.getNights());
                stmt.setString(4, services); // Store the comma-separated string of services
                stmt.setDouble(5, bill.getBaseCharge());
                stmt.setDouble(6, bill.getTax());
                stmt.setDouble(7, bill.getDiscount());
                stmt.setDouble(8, bill.getTotalAmount());

                return stmt.executeUpdate() > 0;

            } catch (SQLException e) {
                System.out.println("Error adding bill: " + e.getMessage());
                return false;
            }
        }

        // Method to convert List<Service> to a comma-separated String (only service names for simplicity)
        private String convertServicesToString(List<Service> services) {
            if (services == null || services.isEmpty()) {
                return "";
            }

            StringBuilder servicesString = new StringBuilder();
            for (Service service : services) {
                servicesString.append(service.getServiceName()).append(", ");  // Only service name for simplicity
            }

            // Remove the last comma and space
            return servicesString.length() > 0 ? servicesString.substring(0, servicesString.length() - 2) : "";
        }

        // ✅ Check if a bill already exists for the given guest
        public boolean isBillAlreadyGenerated(String guestName) {
            String sql = "SELECT COUNT(*) FROM bills WHERE guest_name = ?";
            try (Connection conn = DBConnection.getInstance();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, guestName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            } catch (SQLException e) {
                System.out.println("Error checking if bill already exists: " + e.getMessage());
            }
            return false;
        }

        // Method to retrieve all bills from the database
        public List<Bill> getAllBills() {
            List<Bill> list = new ArrayList<>();
            String sql = "SELECT * FROM bills";

            try (Connection conn = DBConnection.getInstance();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Bill bill = new Bill(
                            rs.getInt("id"),
                            rs.getString("guest_name"),
                            rs.getString("room_type"),
                            rs.getInt("nights"),
                            convertStringToServices(rs.getString("services")), // Convert String to List<Service>
                            rs.getDouble("base_charge"),
                            rs.getDouble("tax"),
                            rs.getDouble("discount"),
                            rs.getDouble("total_amount")
                    );
                    list.add(bill);
                }

            } catch (SQLException e) {
                System.out.println("Error fetching bills: " + e.getMessage());
            }

            return list;
        }

        // Convert the comma-separated string of services back to a List<Service>
// Convert the comma-separated string of services back to a List<Service>
        private List<Service> convertStringToServices(String services) {
            List<Service> serviceList = new ArrayList<>();
            if (services != null && !services.isEmpty()) {
                String[] serviceArray = services.split(", ");
                for (String serviceName : serviceArray) {
                    // ✅ Get real price from DAO
                    serviceList.add(ServiceDAO.getServiceByName(serviceName.trim()));
                }
            }
            return serviceList;
        }
    }
