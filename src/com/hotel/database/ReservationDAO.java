package com.hotel.database;

import com.hotel.models.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    // Add new reservation to DB (now includes late_checkout and group_name)
    public boolean addReservation(Reservation res) {
        String sql = "INSERT INTO reservations (guest_name, check_in, check_out, room_type, payment_status, special_requests, room_number, group_name, late_checkout) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, res.getGuestName());
            stmt.setDate(2, res.getCheckIn());
            stmt.setDate(3, res.getCheckOut());
            stmt.setString(4, res.getRoomType());
            stmt.setString(5, res.getPaymentStatus());
            stmt.setString(6, res.getSpecialRequests());
            stmt.setString(7, res.getRoomNumber());
            stmt.setString(8, res.getGroupName());
            stmt.setBoolean(9, res.isLateCheckout());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting reservation: " + e.getMessage());
            return false;
        }
    }

    // ✅ NEW: Get only guest names that are not part of a group
    public List<String> getOnlyIndividualGuestNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT DISTINCT guest_name FROM reservations WHERE group_name IS NULL ORDER BY guest_name";

        try (Connection conn = DBConnection.getInstance();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                names.add(rs.getString("guest_name"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching individual guest names: " + e.getMessage());
        }

        return names;
    }


    public boolean reservationExists(String guestName, Date checkIn, Date checkOut) {
        String query = "SELECT COUNT(*) FROM reservations WHERE guest_name = ? AND check_in = ? AND check_out = ?";
        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, guestName);
            stmt.setDate(2, checkIn);
            stmt.setDate(3, checkOut);

            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.out.println("Error checking reservation: " + e.getMessage());
            return false;
        }
    }

    public boolean updateReservation(Reservation res) {
        String sql = "UPDATE reservations SET guest_name=?, check_in=?, check_out=?, room_type=?, payment_status=?, special_requests=?, group_name=?, late_checkout=? WHERE id=?";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, res.getGuestName());
            stmt.setDate(2, res.getCheckIn());
            stmt.setDate(3, res.getCheckOut());
            stmt.setString(4, res.getRoomType());
            stmt.setString(5, res.getPaymentStatus());
            stmt.setString(6, res.getSpecialRequests());
            stmt.setString(7, res.getGroupName());
            stmt.setBoolean(8, res.isLateCheckout());
            stmt.setInt(9, res.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating reservation: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteReservationById(int id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting reservation: " + e.getMessage());
            return false;
        }
    }

    public List<String> getAllGuestNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT DISTINCT guest_name FROM reservations ORDER BY guest_name";

        try (Connection conn = DBConnection.getInstance();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                names.add(rs.getString("guest_name"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching guest names: " + e.getMessage());
        }

        return names;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";

        try (Connection conn = DBConnection.getInstance();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reservation res = new Reservation(
                        rs.getInt("id"),
                        rs.getString("guest_name"),
                        rs.getDate("check_in"),
                        rs.getDate("check_out"),
                        rs.getString("room_type"),
                        rs.getString("payment_status"),
                        rs.getString("special_requests"),
                        rs.getString("room_number"),
                        rs.getString("group_name"),
                        rs.getBoolean("late_checkout")
                );
                list.add(res);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching reservations: " + e.getMessage());
        }

        return list;
    }

    public Reservation getReservationByGuestName(String guestName) {
        String sql = "SELECT * FROM reservations WHERE guest_name = ? ORDER BY check_in DESC LIMIT 1";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, guestName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Reservation(
                        rs.getInt("id"),
                        rs.getString("guest_name"),
                        rs.getDate("check_in"),
                        rs.getDate("check_out"),
                        rs.getString("room_type"),
                        rs.getString("payment_status"),
                        rs.getString("special_requests"),
                        rs.getString("room_number"),
                        rs.getString("group_name"),
                        rs.getBoolean("late_checkout")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching reservation by guest name: " + e.getMessage());
        }

        return null;
    }

    public List<Reservation> getReservationsByGroup(String groupName) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE group_name = ?";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation res = new Reservation(
                        rs.getInt("id"),
                        rs.getString("guest_name"),
                        rs.getDate("check_in"),
                        rs.getDate("check_out"),
                        rs.getString("room_type"),
                        rs.getString("payment_status"),
                        rs.getString("special_requests"),
                        rs.getString("room_number"),
                        rs.getString("group_name"),
                        rs.getBoolean("late_checkout")
                );
                list.add(res);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching group reservations: " + e.getMessage());
        }

        return list;
    }

    public List<String> getAllGroupNames() {
        List<String> groupNames = new ArrayList<>();
        String sql = "SELECT DISTINCT group_name FROM reservations WHERE group_name IS NOT NULL";

        try (Connection conn = DBConnection.getInstance();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                groupNames.add(rs.getString("group_name"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching group names: " + e.getMessage());
        }

        return groupNames;
    }
}
