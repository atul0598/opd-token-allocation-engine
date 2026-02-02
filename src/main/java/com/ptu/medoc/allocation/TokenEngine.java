package com.ptu.medoc.allocation;


import com.ptu.medoc.dto.PatientDTO;
import com.ptu.medoc.entity.*;
import com.ptu.medoc.enums.SlotStatus;
import com.ptu.medoc.enums.TokenSource;
import com.ptu.medoc.enums.TokenStatus;
import com.ptu.medoc.exception.ErrorCode;
import com.ptu.medoc.exception.TokenGenerationException;
import com.ptu.medoc.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TokenEngine {

    @Autowired
    private  SlotRepository slotRepo;
    @Autowired
    private  TokenRepository tokenRepo;
    @Autowired
    private  WaitlistRepository waitlistRepo;
    @Autowired
    private  PriorityCalculator calculator;
    @Autowired
    private DoctorRepository docRepo;
    @Autowired
    private PatientRepository patientRepo;

//    Allocating Token to the eligible Patient
    public Map<String, Object> allocate(Doctor doctor, PatientDTO patientDTO, TokenSource source)
    {
        int score = calculator.score(source);

        Patient patient = patientRepo
                .findByPhoneNumber(patientDTO.getPhoneNumber())
                .orElseGet(() -> {
                    Patient p = new Patient();
                    p.setName(patientDTO.getName());
                    p.setPhoneNumber(patientDTO.getPhoneNumber());
                    return patientRepo.save(p);
                });

        // One token per day rule
        if (tokenRepo.existsByPatientIdAndBookingDateAndStatus(
                patient.getId(),
                LocalDate.now(),
                TokenStatus.BOOKED)) {
            throw TokenGenerationException.duplicateToken(patient);
        }

        List<Slot> available = slotRepo.findAvailableSlot(doctor.getId());
        if (!available.isEmpty()) {
            return createToken(available.get(0), patient, source, score);
        }

        //Try PREEMPTION
        List<Token> lowestPriorityTokens =
                tokenRepo.findLowestPriorityForDoctor(
                        doctor.getId(),
                        LocalDate.now()
                );


        if (!lowestPriorityTokens.isEmpty()) {
            Token victim = lowestPriorityTokens.get(0);

            if (victim.getPriorityScore() < score) {

                // cancel old token
                victim.setStatus(TokenStatus.CANCELLED);
                tokenRepo.save(victim);

                Slot slot = victim.getSlot();
                slot.setBookedCount(slot.getBookedCount() - 1);
                slotRepo.save(slot);

                // assign to high-priority patient
                return createToken(slot, patient, source, score);
            }
        }

        Waitlist w = new Waitlist();
        w.setDoctor(doctor);
        w.setPatient(patient);
        w.setSource(source);
        w.setPriorityScore(score);
        w.setBookingDate(LocalDate.now());
        waitlistRepo.save(w);

        Map<String, Object> response = Map.of(
                "patientName", patientDTO,
                "status", "WAITLISTED",
                "message", "Your waiting list number :" + w.getId()
        );

        return response;
    }


//Creating Token Based on availability of Slot

    private Map<String, Object> createToken(Slot slot,
                                            Patient patient,
                                            TokenSource source,
                                            int score) {

        int nextTokenNumber =
                tokenRepo.findMaxTokenNumberForDoctorLocked(
                        slot.getDoctor().getId(),
                        LocalDate.now()
                ) + 1;

        Token token = new Token();
        token.setSlot(slot);
        token.setPatient(patient);
        token.setDoctor(slot.getDoctor());
        token.setSource(source);
        token.setPriorityScore(score);
        token.setTokenNumber(nextTokenNumber);
        token.setBookingDate(LocalDate.now());
        tokenRepo.save(token);

        slot.setBookedCount(slot.getBookedCount() + 1);

        if (slot.getBookedCount() == slot.getMaxCapacity()) {
            slot.setStatus(SlotStatus.FULL);
        }
        slotRepo.save(slot);

        Map<String, Object> response = Map.of(
                "tokenNumber", token.getTokenNumber(),
                "slot:", Map.of("slot-StartTime" ,slot.getStartTime(),
                        "slot-EndTime", slot.getEndTime()),
                "patientName", patient.getName(),
                "doctorName", slot.getDoctor().getName(),
                "status", "BOOKED",
                "message", "Token successfully booked with token number:" + token.getTokenNumber()
        );
        return response;
    }

//    cancel token
  @Transactional
    public Map<String, Object> cancelToken(Long tokenNumber) {
      Token token = tokenRepo
              .findByTokenNumberAndStatusInOrderByCreatedAtDesc(
                      Math.toIntExact(tokenNumber),
                      List.of(TokenStatus.BOOKED)
              )
              .stream()
              .findFirst()
              .orElseThrow(() ->
                      new TokenGenerationException(
                              "Token not found or already cancelled",
                              HttpStatus.NOT_FOUND, ErrorCode.TOKEN_NOT_FOUND
                      )
              );

      // Cancel token
      token.setStatus(TokenStatus.CANCELLED);
      tokenRepo.save(token);

      //find free slot
        Slot slot = token.getSlot();
        slot.setBookedCount(slot.getBookedCount() - 1);
      slot.setStatus(SlotStatus.OPEN);
        slotRepo.save(slot);

        promoteFromWaitlist(slot);

        return Map.of(
                "status", "CANCELLED",
                "message", "Token Number: " +tokenNumber + " cancelled successfully."
        );
    }

//    promoting waitlisted patient
    private void promoteFromWaitlist(Slot slot) {

        LocalDate today = LocalDate.now();

        List<Waitlist> candidates =
                waitlistRepo.findForPromotion(
                        slot.getDoctor().getId(),
                        today.atStartOfDay(),
                        today.atTime(LocalTime.MAX)
                );

        if (candidates.isEmpty()) {
            return;
        }

        for(Waitlist w : candidates) {
            Patient patient = w.getPatient();

            boolean alreadyBooked =
                    tokenRepo.existsByPatientIdAndBookingDateAndStatus(
                            patient.getId(),
                            today,
                            TokenStatus.BOOKED
                    );
            if (alreadyBooked) {
                waitlistRepo.delete(w); // cleanup stale entry
                continue;
            }

            // Allocate token

            int nextTokenNumber =
                    tokenRepo.findMaxTokenNumberForDoctorLocked(
                            slot.getDoctor().getId(),
                            LocalDate.now()
                    ) + 1;

            Token token = new Token();
            token.setSlot(slot);
            token.setDoctor(slot.getDoctor());
            token.setPatient(patient);
            token.setSource(w.getSource());
            token.setPriorityScore(w.getPriorityScore());
            token.setTokenNumber(nextTokenNumber);
            token.setBookingDate(today);
            tokenRepo.save(token);

            slot.setBookedCount(slot.getBookedCount() + 1);
            if (slot.getBookedCount() == slot.getMaxCapacity()) {
                slot.setStatus(SlotStatus.FULL);
            }
            slotRepo.save(slot);

            // Remove from waitlist
            waitlistRepo.delete(w);

            return; // promote ONLY ONE

        }
    }

// mark no show and reassign token to the waitlist patient based on priority

    public Map<String,Object> markNoShowAndReassign(Long tokenId) {

        Token token=tokenRepo.findById(tokenId)
                .orElseThrow(
                        ()-> new TokenGenerationException(
                                "Token not found or already cancelled",
                                HttpStatus.NOT_FOUND, ErrorCode.TOKEN_NOT_FOUND
                        )
                );
        if(token.getStatus()!= TokenStatus.BOOKED){
            return Map.of(
                    "status", "IGNORED",
                    "message", "Only BOOKED tokens can be marked NO_SHOW"
            );
        }
        LocalDateTime graceCutoff =
                token.getSlot().getStartTime().plusMinutes(15);

        if (LocalDateTime.now().isBefore(graceCutoff)) {
            return Map.of(
                    "status", "TOO_EARLY",
                    "message", "Patient still within grace period"
            );
        }

        token.setStatus(TokenStatus.NO_SHOW);
        tokenRepo.save(token);

        Slot slot = token.getSlot();
        slot.setBookedCount(slot.getBookedCount() - 1);
        if (slot.getBookedCount() < slot.getMaxCapacity()) {
            slot.setStatus(SlotStatus.OPEN);
        }
        slotRepo.save(slot);

        promoteFromWaitlist(slot);

        return Map.of(
                "status", "NO_SHOW",
                "message", "Token marked NO_SHOW and slot reassigned"
        );

    }


}
