package com.hotel.database;

import com.hotel.models.HousekeepingTask;
import com.hotel.models.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HousekeepingDAO {

    // Allow rooms marked as Available, Maintenance, or Cleaning
    public boolean isRoomEligibleForCleaning(int roomId) {
        String sql = "SELECT status FROM rooms WHERE id = ?";
        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                return status.equalsIgnoreCase("Available")
                        || status.equalsIgnoreCase("Maintenance")
                        || status.equalsIgnoreCase("Cleaning");
            }
        } catch (SQLException e) {
            System.out.println("Error checking room status: " + e.getMessage());
        }
        return false;
    }

    public boolean addTask(HousekeepingTask task) {
        String checkSql = "SELECT COUNT(*) FROM housekeeping_tasks WHERE room_id = ? AND assigned_to = ? AND status = ?";
        String insertSql = "INSERT INTO housekeeping_tasks (room_id, assigned_to, status, scheduled_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            // Avoid duplicate task entries
            checkStmt.setInt(1, task.getRoomId());
            checkStmt.setString(2, task.getAssignedTo());
            checkStmt.setString(3, task.getStatus());

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("⚠️ Duplicate housekeeping task detected.");
                return false;
            }

            // Insert housekeeping task
            insertStmt.setInt(1, task.getRoomId());
            insertStmt.setString(2, task.getAssignedTo());
            insertStmt.setString(3, task.getStatus());
            insertStmt.setDate(4, task.getScheduledDate());

            return insertStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding housekeeping task: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(int taskId, String newStatus) {
        String sql = "UPDATE housekeeping_tasks SET status = ? WHERE id = ?";
        String roomStatusUpdateSql = "UPDATE rooms SET status = 'Ready' WHERE id = (SELECT room_id FROM housekeeping_tasks WHERE id = ?)";

        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql);
             PreparedStatement roomStmt = conn.prepareStatement(roomStatusUpdateSql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, taskId);
            int updated = stmt.executeUpdate();

            if (updated > 0 && newStatus.equalsIgnoreCase("Completed")) {
                roomStmt.setInt(1, taskId); // Update room to 'Ready'
                roomStmt.executeUpdate();
                System.out.println("✅ Task completed. Room status set to Ready.");
            }

            return updated > 0;

        } catch (SQLException e) {
            System.out.println("Error updating task: " + e.getMessage());
            return false;
        }
    }

    public List<HousekeepingTask> getAllTasks() {
        List<HousekeepingTask> list = new ArrayList<>();
        String sql = "SELECT * FROM housekeeping_tasks";

        try (Connection conn = DBConnection.getInstance();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                HousekeepingTask task = new HousekeepingTask(
                        rs.getInt("id"),
                        rs.getInt("room_id"),
                        rs.getString("assigned_to"),
                        rs.getString("status"),
                        rs.getDate("scheduled_date")
                );
                list.add(task);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching tasks: " + e.getMessage());
        }

        return list;
    }

    public HousekeepingTask getTaskById(int taskId) {
        String sql = "SELECT * FROM housekeeping_tasks WHERE id = ?";
        try (Connection conn = DBConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, taskId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new HousekeepingTask(
                        rs.getInt("id"),
                        rs.getInt("room_id"),
                        rs.getString("assigned_to"),
                        rs.getString("status"),
                        rs.getDate("scheduled_date")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching task by ID: " + e.getMessage());
        }
        return null;
    }
}
