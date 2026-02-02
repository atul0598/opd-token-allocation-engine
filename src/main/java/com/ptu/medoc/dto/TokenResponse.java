package com.ptu.medoc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String tokenNumber;
    private String status;

}
