package com.ptu.medoc.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ptu.medoc.enums.TokenStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonPropertyOrder({
        "tokenNumber",
        "patientName",
        "doctorName",
        "startTime",
        "endTime",
        "status",
        "createdAt"
})
public class BookedTokenResponse {
    private int tokenNumber;
    private String patientName;
    private String doctorName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TokenStatus status;
    private LocalDateTime createdAt;
}
