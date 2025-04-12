package com.hotel.models;

import java.sql.Date;

public class Reservation {
    private int id;
    private String guestName;
    private Date checkIn;
    private Date checkOut;
    private String roomType;
    private String paymentStatus;
    private String specialRequests;
    private String roomNumber;
    private String groupName;
    private boolean lateCheckout; // ✅ New field

    // Constructor without ID
    public Reservation(String guestName, Date checkIn, Date checkOut, String roomType,
                       String paymentStatus, String specialRequests, String roomNumber,
                       String groupName, boolean lateCheckout) {
        this.guestName = guestName;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.roomType = roomType;
        this.paymentStatus = paymentStatus;
        this.specialRequests = specialRequests;
        this.roomNumber = roomNumber;
        this.groupName = groupName;
        this.lateCheckout = lateCheckout;
    }

    // Constructor with ID
    public Reservation(int id, String guestName, Date checkIn, Date checkOut, String roomType,
                       String paymentStatus, String specialRequests, String roomNumber,
                       String groupName, boolean lateCheckout) {
        this(guestName, checkIn, checkOut, roomType, paymentStatus, specialRequests, roomNumber, groupName, lateCheckout);
        this.id = id;
    }

    // Existing constructor without groupName or lateCheckout
    public Reservation(String guestName, Date checkIn, Date checkOut, String roomType,
                       String paymentStatus, String specialRequests, String roomNumber) {
        this(guestName, checkIn, checkOut, roomType, paymentStatus, specialRequests, roomNumber, null, false);
    }

    // Existing constructor with ID without groupName or lateCheckout
    public Reservation(int id, String guestName, Date checkIn, Date checkOut, String roomType,
                       String paymentStatus, String specialRequests, String roomNumber) {
        this(guestName, checkIn, checkOut, roomType, paymentStatus, specialRequests, roomNumber, null, false);
        this.id = id;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public Date getCheckIn() { return checkIn; }
    public void setCheckIn(Date checkIn) { this.checkIn = checkIn; }

    public Date getCheckOut() { return checkOut; }
    public void setCheckOut(Date checkOut) { this.checkOut = checkOut; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public boolean isLateCheckout() { return lateCheckout; }
    public void setLateCheckout(boolean lateCheckout) { this.lateCheckout = lateCheckout; }
}
