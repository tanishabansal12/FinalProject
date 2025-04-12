package com.hotel.roles;

import com.hotel.models.User;
import com.hotel.views.MainMenuView;

public class ReceptionistRole implements UserRole {

    private final User user;

    public ReceptionistRole(User user) {
        this.user = user;
    }

    @Override
    public void launchDashboard() {
        new MainMenuView(user);
    }
}
