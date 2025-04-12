    package com.hotel.models;

    import java.sql.Date;

    public class HousekeepingTask {
        private int id;
        private int roomId;
        private String assignedTo;
        private String status; // Pending, In Progress, Completed
        private Date scheduledDate;

        public HousekeepingTask(int roomId, String assignedTo, String status, Date scheduledDate) {
            this.roomId = roomId;
            this.assignedTo = assignedTo;
            this.status = status;
            this.scheduledDate = scheduledDate;
        }

        public HousekeepingTask(int id, int roomId, String assignedTo, String status, Date scheduledDate) {
            this(roomId, assignedTo, status, scheduledDate);
            this.id = id;
        }

        // Getters and Setters
        public int getId() { return id; }
        public int getRoomId() { return roomId; }
        public String getAssignedTo() { return assignedTo; }
        public String getStatus() { return status; }
        public Date getScheduledDate() { return scheduledDate; }

        public void setId(int id) { this.id = id; }
        public void setRoomId(int roomId) { this.roomId = roomId; }
        public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
        public void setStatus(String status) { this.status = status; }
        public void setScheduledDate(Date scheduledDate) { this.scheduledDate = scheduledDate; }
    }
