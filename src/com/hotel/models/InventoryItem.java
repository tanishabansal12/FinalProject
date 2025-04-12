package com.hotel.models;

public class InventoryItem {
    private int id;
    private String itemName;
    private int quantity;
    private int threshold;
    private String department; // New field for department

    // Constructor with department
    public InventoryItem(int id, String itemName, int quantity, int threshold, String department) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.threshold = threshold;
        this.department = department;  // Set the department
    }

    // Constructor without department (used in cases where department isn't set)
    public InventoryItem(String itemName, int quantity, int threshold, String department) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.threshold = threshold;
        this.department = department;
    }

    // Constructor without department field (for backward compatibility)
    public InventoryItem(int id, String itemName, int quantity, int threshold) {
        this(itemName, quantity, threshold, "Uncategorized"); // Default to "Uncategorized" if department is not specified
        this.id = id;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public int getThreshold() { return threshold; }
    public String getDepartment() { return department; }  // Getter for department

    public void setId(int id) { this.id = id; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setThreshold(int threshold) { this.threshold = threshold; }
    public void setDepartment(String department) { this.department = department; }  // Setter for department
}
