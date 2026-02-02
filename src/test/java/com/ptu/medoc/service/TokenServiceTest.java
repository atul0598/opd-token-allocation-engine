package com.ptu.medoc.service;

import com.ptu.medoc.allocation.TokenEngine;
import com.ptu.medoc.dto.BookedTokenResponse;
import com.ptu.medoc.dto.TokenRequest;
import com.ptu.medoc.entity.*;
import com.ptu.medoc.enums.TokenStatus;
import com.ptu.medoc.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TokenServiceTest {

    @Mock private SlotRepository slotRepo;
    @Mock private TokenRepository tokenRepo;
    @Mock private WaitlistRepository waitlistRepo;
    @Mock private DoctorRepository doctorRepo;
    @Mock private TokenEngine tokenEngine;
    @Mock private DuplicateTokenValidationService duplicateTokenValidationService;
    @Mock private PatientRepository patientRepo;
    @Mock private tools.jackson.databind.cfg.MapperBuilder mapperBuilder;

    @InjectMocks
    private TokenService tokenService;

    @Test
    void allocate_success() {
        TokenRequest request = new TokenRequest();
        request.setDoctorName("Dr John");
        request.setNameOfPatient("Alice");
        request.setPhoneNumber("999");
        request.setSource("ONLINE");

        Doctor doctor = new Doctor();
        doctor.setName("Dr John");

        when(doctorRepo.findByNameIgnoreCase("Dr John"))
                .thenReturn(Optional.of(doctor));

        when(tokenEngine.allocate(any(), any(), any()))
                .thenReturn(Map.of("status", "BOOKED"));

        Map<String, Object> response = tokenService.allocate(request);

        assertThat(response.get("status")).isEqualTo("BOOKED");
    }

    @Test
    void getBookedTokens_returnsPage() {
        Token token = new Token();
        token.setStatus(TokenStatus.BOOKED);
        token.setCreatedAt(LocalDateTime.now());

        Patient p = new Patient();
        p.setName("Bob");
        token.setPatient(p);

        Slot s = new Slot();
        Doctor d = new Doctor();
        d.setName("Dr A");
        s.setDoctor(d);
        token.setSlot(s);

        Page<Token> tokenPage =
                new PageImpl<>(List.of(token));

        when(tokenRepo.findByStatusAndCreatedAtBetween(
                eq(TokenStatus.BOOKED),
                any(), any(), any()))
                .thenReturn(tokenPage);

        Page<BookedTokenResponse> result =
                tokenService.getBookedTokens(
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now(),
                        PageRequest.of(0, 10)
                );

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getPatientName()).isEqualTo("Bob");
    }
}
