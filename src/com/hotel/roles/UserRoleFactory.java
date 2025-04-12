package com.hotel.roles;

import com.hotel.models.User;
import com.hotel.roles.AdminRole;
import com.hotel.roles.HousekeepingRole;
import com.hotel.roles.ReceptionistRole;

public class UserRoleFactory {

    public static UserRole getRole(User user) {
        switch (user.getRole().toLowerCase()) {
            case "admin":
                return new AdminRole(user);
            case "housekeeping":
                return new HousekeepingRole(user);
            case "receptionist":
                return new ReceptionistRole(user);
            default:
                return null;
        }
    }
}
