package com.hotel.models;

public class Room {
    private int id;
    private String roomNumber;
    private String type;
    private String status; // Available, Occupied, Maintenance

    public Room(String roomNumber, String type, String status) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.status = status;
    }

    public Room(int id, String roomNumber, String type, String status) {
        this(roomNumber, type, status);
        this.id = id;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public String getStatus() { return status; }

    public void setId(int id) { this.id = id; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setType(String type) { this.type = type; }
    public void setStatus(String status) { this.status = status; }
}
