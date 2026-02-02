package com.ptu.medoc.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column( nullable = false, unique = true)
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number format")
    private String phoneNumber;
    @Column(nullable = false)
    private LocalDateTime bookingDate = LocalDateTime.now();
}
