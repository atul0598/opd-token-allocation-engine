package com.ptu.medoc.service;

import com.ptu.medoc.entity.Patient;
import com.ptu.medoc.entity.Slot;
import com.ptu.medoc.enums.TokenStatus;
import com.ptu.medoc.exception.DuplicateTokenException;
import com.ptu.medoc.repository.PatientRepository;
import com.ptu.medoc.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DuplicateTokenValidationService {

    private final TokenRepository tokenRepository;

    private static final List<TokenStatus> ACTIVE_STATUSES =
            List.of(
                    TokenStatus.BOOKED,
                    TokenStatus.IN_PROGRESS
            );

    public void duplicateTokenCheck(Patient patient, Slot slots) throws DuplicateTokenException {
        boolean alreadyBooked =
                tokenRepository.existsByPatientAndSlotAndStatusIn(patient, slots, ACTIVE_STATUSES
                );

        if(alreadyBooked){
            throw new DuplicateTokenException(
                    "Patient already has a token for this slot"
            );
        }
    }


}
