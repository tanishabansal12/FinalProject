package com.hotel.patterns;

import java.util.List;

public interface InventoryObserver {
    void update(List<String> lowStockItems);
}
