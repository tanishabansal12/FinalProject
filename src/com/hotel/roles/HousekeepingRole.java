package com.hotel.roles;

import com.hotel.models.User;
import com.hotel.views.MainMenuView;

public class HousekeepingRole implements UserRole {

    private final User user;

    public HousekeepingRole(User user) {
        this.user = user;
    }

    @Override
    public void launchDashboard() {
        new MainMenuView(user);
    }
}
