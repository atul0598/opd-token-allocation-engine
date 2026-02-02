package com.ptu.medoc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.ptu.medoc.exception.TokenGenerationException;
import lombok.Getter;

@Getter
public enum TokenSource {
    ONLINE("Online"),
    WALK_IN("Walk-in"),
    PAID_PRIORITY("Paid-Priority"),
    FOLLOW_UP("Follow-up"),
    EMERGENCY("Emergency");

    TokenSource(String displayName) {
        this.displayName = displayName;
    }
    private final String displayName;

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static TokenSource from(String value) throws Exception {

        try {
            return TokenSource.valueOf(value.toUpperCase());
        } catch (Exception ex) {

            throw TokenGenerationException.invalidTokenState(
                    "Invalid token source: " + value
            );
        }
    }
}
