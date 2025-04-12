package com.hotel.database;

import com.hotel.models.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, type, status) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getType());
            stmt.setString(3, room.getStatus());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding room: " + e.getMessage());
            return false;
        }
    }

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms";

        try (Connection conn = DBConnection.getInstance();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getString("type"),
                        rs.getString("status")
                );
                list.add(room);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching rooms: " + e.getMessage());
        }

        return list;
    }

    public Room getAvailableRoom() {
        String sql = "SELECT * FROM rooms WHERE status = 'Available' LIMIT 1";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getString("type"),
                        rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching available room: " + e.getMessage());
        }

        return null;
    }

    // Get Room by ID (for use in housekeeping)
    public Room getRoomById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getString("type"),
                        rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching room by ID: " + e.getMessage());
        }
        return null;
    }


    public Room getAvailableRoomByType(String type) {
        String sql = "SELECT * FROM rooms WHERE type = ? AND status = 'Available' LIMIT 1";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getString("type"),
                        rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching room by type: " + e.getMessage());
        }

        return null;
    }

    public boolean updateRoomStatus(int roomId, String newStatus) {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, roomId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating room status: " + e.getMessage());
            return false;
        }
    }

    public void updateRoomStatusToOccupied(int roomId) {
        updateRoomStatus(roomId, "Occupied");
    }

    // ✅ Check if room ID exists
    public boolean roomIdExists(int roomId) {
        String sql = "SELECT COUNT(*) FROM rooms WHERE id = ?";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error checking room ID: " + e.getMessage());
        }

        return false;
    }

    // ✅ NEW: Mark room as Available (used during Check-Out)
    public void updateRoomStatusToAvailable(int roomId) {
        updateRoomStatus(roomId, "Available");
    }

    // ✅ NEW: Get Room by its room number (for Check-Out reverse lookup)
    public Room getRoomByNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roomNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Room(
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getString("type"),
                        rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching room by number: " + e.getMessage());
        }

        return null;
    }
}
