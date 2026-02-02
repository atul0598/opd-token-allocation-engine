package com.ptu.medoc.enums;

import lombok.Getter;

@Getter
public enum AllocationStatus {
    BOOKED("Booked"),
    WAITLISTED("Waitlisted"),
    PREEMPTED("Preempted");
    private final String displayName;
    AllocationStatus(String displayName) {
        this.displayName = displayName;
    }

}
