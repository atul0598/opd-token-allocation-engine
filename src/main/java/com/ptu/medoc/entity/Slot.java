package com.ptu.medoc.entity;

import com.ptu.medoc.enums.SlotStatus;
import com.ptu.medoc.enums.TokenStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.PriorityQueue;

@Entity
@Getter
@Setter
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Doctor doctor;
    private Long SlotNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private int maxCapacity;
    private int bookedCount = 0;

    @Enumerated(EnumType.STRING)
    private SlotStatus status = SlotStatus.OPEN;


    private final PriorityQueue<Token> activeTokens =
            new PriorityQueue<>(
                    Comparator
                            .comparingInt(Token::getPriorityScore)
                            .thenComparing(Token::getCreatedAt)
            );

    private final PriorityQueue<Token> waitlistTokens =
            new PriorityQueue<>(
                    Comparator
                            .comparingInt(Token::getPriorityScore)
                            .reversed()
                            .thenComparing(Token::getCreatedAt)
            );

    public boolean hasFreeCapacity() {
        return activeTokens.size() < maxCapacity;
    }

    public void addActiveToken(Token token) {
        token.setStatus(TokenStatus.BOOKED);
        activeTokens.add(token);
    }

    public void addToWaitlist(Token token) {
        token.setStatus(TokenStatus.IN_PROGRESS);
        waitlistTokens.add(token);
    }

    public Token getLowestPriorityToken() {
        return activeTokens.peek();
    }

    public Token removeActiveToken(Long Id) {
        for (Token token : activeTokens) {
            if (token.getId().equals(Id)) {
                activeTokens.remove(token);
                return token;
            }
        }
        return null;
    }

    public Token promoteFromWaitlist() {
        if (waitlistTokens.isEmpty()) {
            return null;
        }
        Token token = waitlistTokens.poll();
        addActiveToken(token);
        return token;
    }


//    for preventing race conditions during token allocation
    @Version
    private Long version;

}
