package com.hotel.patterns;

import java.util.ArrayList;
import java.util.List;

public class InventoryNotifier {
    private static final List<InventoryObserver> observers = new ArrayList<>();

    public static void addObserver(InventoryObserver observer) {
        observers.add(observer);
    }

    public static void notifyObservers(List<String> lowStockItems) {
        for (InventoryObserver observer : observers) {
            observer.update(lowStockItems);
        }
    }
}
