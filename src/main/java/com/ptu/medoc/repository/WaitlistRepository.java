package com.ptu.medoc.repository;

import com.ptu.medoc.entity.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist,Long> {
//    @Query("""
//           SELECT w
//           FROM Waitlist w
//           WHERE w.doctor.id = :doctorId
//           ORDER BY w.priorityScore DESC, w.createdAt ASC
//           """)

    List<Waitlist> findByDoctorIdOrderByPriorityScoreDescCreatedAtAsc(Long doctorId);

    @Query("""
    SELECT w FROM Waitlist w
    WHERE w.doctor.id = :doctorId
      AND w.createdAt >= :startOfDay
      AND w.createdAt <= :endOfDay
    ORDER BY w.priorityScore DESC, w.createdAt ASC
""")
    List<Waitlist> findForPromotion(
            Long doctorId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

}
