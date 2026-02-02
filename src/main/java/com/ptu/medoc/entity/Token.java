package com.ptu.medoc.entity;
import com.ptu.medoc.enums.TokenSource;
import com.ptu.medoc.enums.TokenStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "token",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"patient_id", "booking_date"}
        )
)
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Slot slot;

    @ManyToOne(optional = false)
    private Patient patient;

    @ManyToOne(optional = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    private TokenSource source;

    private int priorityScore;

    private int tokenNumber;

    @Enumerated(EnumType.STRING)
    private TokenStatus status = TokenStatus.BOOKED;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    public boolean isActive() {
        return status == TokenStatus.BOOKED;
    }

    public void cancel() {
        this.status = TokenStatus.CANCELLED;
    }

    public void markNoShow() {
        this.status = TokenStatus.NO_SHOW;
    }
}

