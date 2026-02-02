package com.ptu.medoc.dto;

import com.ptu.medoc.enums.AllocationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AllocationResult {
    private String tokenNumber;
    private AllocationStatus status;
}
