package com.ptu.medoc.enums;

import lombok.Getter;

@Getter
public enum TokenStatus {
    BOOKED("Booked"),
    IN_PROGRESS("In Progress"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed"),
    NO_SHOW("No Show");
    private final String displayName;
    TokenStatus(String displayName) {
        this.displayName = displayName;

    }
}
