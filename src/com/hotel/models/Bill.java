    package com.hotel.models;

    import java.util.List;

    public class Bill {
        private int id;
        private String guestName;
        private String roomType;
        private int nights;
        private List<Service> services;  // Store services as List<Service>
        private double baseCharge;
        private double tax;
        private double discount;
        private double totalAmount;

        public Bill(String guestName, String roomType, int nights, List<Service> services,
                    double baseCharge, double tax, double discount, double totalAmount) {
            this.guestName = guestName;
            this.roomType = roomType;
            this.nights = nights;
            this.services = services;  // List<Service>
            this.baseCharge = baseCharge;
            this.tax = tax;
            this.discount = discount;
            this.totalAmount = totalAmount;
        }

        public Bill(int id, String guestName, String roomType, int nights, List<Service> services,
                    double baseCharge, double tax, double discount, double totalAmount) {
            this(guestName, roomType, nights, services, baseCharge, tax, discount, totalAmount);
            this.id = id;
        }

        // Getters and Setters
        public int getId() { return id; }
        public String getGuestName() { return guestName; }
        public String getRoomType() { return roomType; }
        public int getNights() { return nights; }
        public List<Service> getServices() { return services; }
        public double getBaseCharge() { return baseCharge; }
        public double getTax() { return tax; }
        public double getDiscount() { return discount; }
        public double getTotalAmount() { return totalAmount; }

        public void setId(int id) { this.id = id; }
        public void setGuestName(String guestName) { this.guestName = guestName; }
        public void setRoomType(String roomType) { this.roomType = roomType; }
        public void setNights(int nights) { this.nights = nights; }
        public void setServices(List<Service> services) { this.services = services; }
        public void setBaseCharge(double baseCharge) { this.baseCharge = baseCharge; }
        public void setTax(double tax) { this.tax = tax; }
        public void setDiscount(double discount) { this.discount = discount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    }
