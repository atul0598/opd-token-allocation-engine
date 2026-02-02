package com.ptu.medoc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class CanceledToken {
    @Id
    private Long id;

    private int tokenNumber;

    @ManyToOne(optional = false)
    private Patient patient;

    @ManyToOne(optional = false)
    private Doctor doctor;

    private String reason;

    private LocalDateTime canceledAt = LocalDateTime.now();
}
