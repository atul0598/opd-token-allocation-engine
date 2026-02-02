

INSERT INTO doctor (id, name, specialization, status) VALUES
     (1, 'Dr Sharma', 'Cardiology', 'Active'),
     (2, 'Dr Mehta', 'ENT', 'Active'),
    (3, 'Dr Khan', 'Neurology', 'Active');
INSERT INTO slot
(id, doctor_id, slot_number,start_time, end_time, max_capacity, booked_count, status, version)
VALUES
-- Doctor 1
(1, 1, 1,'2026-02-01 09:00:00', '2026-02-01 10:00:00', 1, 0, 'OPEN', 0),
(2, 1, 2, '2026-02-01 10:00:00', '2026-02-01 11:00:00', 1, 0, 'OPEN', 0),

-- Doctor 2
(3, 2, 1,'2026-02-01 09:00:00', '2026-02-01 10:00:00', 1, 0, 'OPEN', 0),
(4, 2, 2,'2026-02-01 10:00:00', '2026-02-01 11:00:00', 1, 0, 'OPEN', 0),

-- Doctor 3
(5, 3, 1, '2026-02-01 09:00:00', '2026-02-01 10:00:00', 1, 0, 'OPEN', 0),
(6, 3, 2, '2026-02-01 10:00:00', '2026-02-01 11:00:00', 1, 0, 'OPEN', 0);
