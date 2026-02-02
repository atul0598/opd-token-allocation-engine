package com.ptu.medoc.service;

import com.ptu.medoc.allocation.PriorityCalculator;
import com.ptu.medoc.allocation.TokenEngine;
import com.ptu.medoc.dto.AllocationResult;
import com.ptu.medoc.dto.BookedTokenResponse;
import com.ptu.medoc.dto.PatientDTO;
import com.ptu.medoc.dto.TokenRequest;
import com.ptu.medoc.entity.*;
import com.ptu.medoc.enums.AllocationStatus;
import com.ptu.medoc.enums.SlotStatus;
import com.ptu.medoc.enums.TokenSource;
import com.ptu.medoc.enums.TokenStatus;
import com.ptu.medoc.exception.DuplicateTokenException;
import com.ptu.medoc.exception.TokenGenerationException;
import com.ptu.medoc.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.cfg.MapperBuilder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenService {

    private final SlotRepository slotRepo;
    private final TokenRepository tokenRepo;
    private final WaitlistRepository waitlistRepo;
    private final PriorityCalculator calculator;
    private final DoctorRepository doctorRepo;
    private final TokenEngine tokenEngine;
    private final DuplicateTokenValidationService duplicateTokenValidationService;
    private final PatientRepository patientRepo;
    private final MapperBuilder mapperBuilder;

//    get booked tokens
public Page<BookedTokenResponse> getBookedTokens(
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable

) {
        Page<Token> tokenPage= tokenRepo.findByStatusAndCreatedAtBetween(
                TokenStatus.BOOKED,
                startDate,
                endDate,
                pageable
        );
        return tokenPage.map(this::mapToDto);
    }
    private BookedTokenResponse mapToDto(Token token) {
        BookedTokenResponse dto = new BookedTokenResponse();
        dto.setTokenNumber(token.getTokenNumber());
        dto.setPatientName(token.getPatient().getName());
        Slot slot = token.getSlot();
        dto.setDoctorName(slot.getDoctor().getName());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setStatus(token.getStatus());
        dto.setCreatedAt(token.getCreatedAt());
        return dto;
    }

//    token allocation logic
    @Transactional
    public Map<String, Object> allocate(TokenRequest request) {

        Doctor doctor = doctorRepo.findByNameIgnoreCase(request.getDoctorName())
                .orElseThrow(() ->
                        TokenGenerationException
                                .doctorNotFound(request.getDoctorName()
                ));

        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setName(request.getNameOfPatient());
        patientDTO.setPhoneNumber(request.getPhoneNumber());

        return (Map<String, Object>) tokenEngine.allocate(
                doctor,
                patientDTO,
                TokenSource.valueOf(request.getSource())
        );
    }
}
