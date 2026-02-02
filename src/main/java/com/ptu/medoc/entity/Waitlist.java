package com.ptu.medoc.entity;

import com.ptu.medoc.enums.TokenSource;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "Waitlist",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"patient_id", "booking_date"}
        )
)
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Doctor doctor;

    @ManyToOne(optional = false)
    private Patient patient;

    @Enumerated(EnumType.STRING)
    private TokenSource source;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    private int priorityScore;

    private LocalDateTime createdAt = LocalDateTime.now();
}
