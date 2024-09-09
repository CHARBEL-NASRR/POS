package com.example.projectt;

public class DatabaseHandler {

    private static user currentUser;

    public static user getCurrentUser() {
        return currentUser;
    }
    public static void setCurrentUser(user user) {
        currentUser = user;
    }
}
