package com.mckinneyjaiden.bengalsseahawksapp.model;

public enum Team {
    BENGALS("Bengals"),
    SEAHAWKS("Seahawks");

    private final String displayName;

    Team(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
