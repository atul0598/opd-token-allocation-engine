package com.ptu.medoc.repository;

import com.ptu.medoc.entity.Patient;
import com.ptu.medoc.entity.Slot;
import com.ptu.medoc.entity.Token;
import com.ptu.medoc.enums.TokenSource;
import com.ptu.medoc.enums.TokenStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {


    @Query("""
        SELECT t FROM Token t
        WHERE t.slot.id = :slotId
        AND t.status = 'BOOKED'
        ORDER BY t.priorityScore ASC
    """)
    List<Token> findLowestPriority(Long slotId);

    List<Token> findByTokenNumberAndStatusInOrderByCreatedAtDesc(
            int tokenNumber,
            List<TokenStatus> statuses

    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    SELECT t FROM Token t
    WHERE t.doctor.id = :doctorId
      AND t.bookingDate = :bookingDate
      AND t.status = 'BOOKED'
    ORDER BY t.priorityScore ASC, t.createdAt ASC
""")
    List<Token> findLowestPriorityForDoctor(
            Long doctorId,
            LocalDate bookingDate
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT COALESCE(MAX(t.tokenNumber), 0)
        FROM Token t
        WHERE t.doctor.id = :doctorId
          AND t.bookingDate = :bookingDate
    """)
    int findMaxTokenNumberForDoctorLocked(
            Long doctorId,
            LocalDate bookingDate
    );

    Page<Token> findByStatusAndCreatedAtBetween(TokenStatus status, LocalDateTime startDate, LocalDateTime endDate,Pageable pageable);

    boolean existsByPatientIdAndBookingDateAndStatus(
            Long patientId,
            LocalDate bookingDate,
            TokenStatus status
    );

    boolean existsByPatientAndSlotAndStatusIn(Patient patient, Slot slot, Collection<TokenStatus> statuses);
}
