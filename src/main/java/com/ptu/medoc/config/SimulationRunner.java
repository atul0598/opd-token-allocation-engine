package com.ptu.medoc.config;

import com.ptu.medoc.allocation.TokenEngine;
import com.ptu.medoc.dto.PatientDTO;
import com.ptu.medoc.entity.Doctor;
import com.ptu.medoc.entity.Slot;
import com.ptu.medoc.enums.TokenSource;
import com.ptu.medoc.repository.DoctorRepository;
import com.ptu.medoc.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SimulationRunner implements CommandLineRunner{

    private final DoctorRepository doctorRepo;
    private final SlotRepository slotRepo;
    private final TokenEngine allocator;

    @Override
    public void run(String... args) {

        // Prevent duplicate data if app restarts
        if (doctorRepo.count() > 0) {
            return;
        }

        Doctor d1 = doctorRepo.save(new Doctor());
        Doctor d2 = doctorRepo.save(new Doctor());
        Doctor d3 = doctorRepo.save(new Doctor());

        createSlots(d1);
        createSlots(d2);
        createSlots(d3);

        // Simulate normal bookings
        for(int i = 0; i < 12; i++) {
            allocator.allocate(
                    doctorRepo.getOne(d1.getId()),
                    buildPatient("Online" + i, i),
                    TokenSource.ONLINE
            );
        }

        // Higher priority bookings
        allocator.allocate(
                doctorRepo.getOne(d2.getId()),
                buildPatient("VIP", 100),
                TokenSource.PAID_PRIORITY
        );

        allocator.allocate(
                doctorRepo.getOne(d3.getId()),
                buildPatient("Emergency", 200),
                TokenSource.EMERGENCY
        );
    }

    private void createSlots(Doctor doctor){

        slotRepo.save(buildSlot(doctor, 9));
        slotRepo.save(buildSlot(doctor, 10));
    }

    private Slot buildSlot(Doctor doctor, int hour){

        Slot s = new Slot();
        s.setDoctor(doctor);
        s.setStartTime(LocalDateTime.now().withHour(hour).withMinute(0));
        s.setEndTime(LocalDateTime.now().withHour(hour + 1).withMinute(0));
        s.setMaxCapacity(10);

        return s;
    }

    private PatientDTO buildPatient(String name, int uniqueNumber){

        PatientDTO dto = new PatientDTO();
        dto.setName(name);

        // ensures unique phone numbers
        dto.setPhoneNumber("9000000" + uniqueNumber);

        return dto;
    }}
