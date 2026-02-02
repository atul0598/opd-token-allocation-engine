package com.ptu.medoc.repository;

import com.ptu.medoc.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT s FROM Slot s WHERE s.id = :slotId")

    // keep your existing methods
    @Query("""
        SELECT s FROM Slot s
        WHERE s.doctor.id = :doctorId
        AND s.maxCapacity <> s.bookedCount
        ORDER BY s.startTime ASC
    """)

    List<Slot> findAvailableSlot(Long doctorId);
//    Slot findByIdForUpdate(@Param("slotId") Long slotId);

    List<Slot> findByDoctorId(Long doctorId);
}

