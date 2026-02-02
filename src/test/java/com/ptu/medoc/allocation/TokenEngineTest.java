package com.ptu.medoc.allocation;

import com.ptu.medoc.dto.PatientDTO;
import com.ptu.medoc.entity.*;
import com.ptu.medoc.enums.*;
import com.ptu.medoc.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TokenEngineTest {

    @Mock private SlotRepository slotRepo;
    @Mock private TokenRepository tokenRepo;
    @Mock private WaitlistRepository waitlistRepo;
    @Mock private PriorityCalculator calculator;
    @Mock private DoctorRepository docRepo;
    @Mock private PatientRepository patientRepo;

    @InjectMocks
    private TokenEngine tokenEngine;

    @Test
    void allocate_createsToken_whenSlotAvailable() {

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr John");

        Slot slot = new Slot();
        slot.setDoctor(doctor);
        slot.setMaxCapacity(5);
        slot.setBookedCount(0);
        slot.setStartTime(LocalDateTime.now().plusMinutes(10));
        slot.setEndTime(LocalDateTime.now().plusMinutes(20));

        PatientDTO dto = new PatientDTO();
        dto.setName("Alice");
        dto.setPhoneNumber("999");

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("Alice");

        when(calculator.score(TokenSource.ONLINE)).thenReturn(10);
        when(patientRepo.findByPhoneNumber("999"))
                .thenReturn(Optional.of(patient));
        when(tokenRepo.existsByPatientIdAndBookingDateAndStatus(
                any(), any(), any()))
                .thenReturn(false);
        when(slotRepo.findAvailableSlot(1L))
                .thenReturn(List.of(slot));
        when(tokenRepo.findMaxTokenNumberForDoctorLocked(any(), any()))
                .thenReturn(0);

        Map<String, Object> response =
                tokenEngine.allocate(doctor, dto, TokenSource.ONLINE);

        assertThat(response.get("status")).isEqualTo("BOOKED");
    }


    @Test
    void cancelToken_success() {
        Token token = new Token();
        token.setStatus(TokenStatus.BOOKED);
        token.setTokenNumber(1);

        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Slot slot = new Slot();
        slot.setDoctor(doctor);
        slot.setBookedCount(1);
        slot.setMaxCapacity(5);
        token.setSlot(slot);

        when(tokenRepo.findByTokenNumberAndStatusInOrderByCreatedAtDesc(
                1, List.of(TokenStatus.BOOKED)))
                .thenReturn(List.of(token));

        Map<String, Object> response =
                tokenEngine.cancelToken(1L);

        assertThat(response.get("status")).isEqualTo("CANCELLED");
    }
}

