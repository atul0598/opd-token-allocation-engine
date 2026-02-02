package com.ptu.medoc.enums;

import lombok.Getter;

@Getter
public enum SlotStatus {
    OPEN("Open"),
    FULL("Full"),
    CLOSED("Closed"),
    DELAYED("Delayed");
    private final String displayName;
    SlotStatus(String displayName) {
        this.displayName = displayName;
    }

}
