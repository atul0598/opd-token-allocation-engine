package com.ptu.medoc.allocation;

import com.ptu.medoc.enums.TokenSource;
import org.springframework.stereotype.Component;

import static com.ptu.medoc.enums.TokenSource.*;

@Component
public class PriorityCalculator {
    public int score(TokenSource source) {

        return switch (source) {
            case EMERGENCY -> 10;
            case PAID_PRIORITY -> 8;
            case FOLLOW_UP -> 5;
            case ONLINE -> 4;
            case WALK_IN -> 2;
        };
    }
}
