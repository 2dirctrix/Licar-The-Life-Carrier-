-- 간호사 테이블
INSERT INTO nurses (name, department) VALUES
('김성재', '비뇨의학과'),
('박지훈', '호흡기내과'),
('정하균', '외상외과');

-- 약사 테이블
INSERT INTO pharmacists (name) VALUES
('이준선'),
('양다인');

-- 환자 테이블 (간호사 테이블의 nurse_id 참조)
INSERT INTO patients (name, birthday, admission_date, room_number, nurse_id) VALUES
('유준호', '2010-02-20', '2025-09-16', 301, 3),
('유란', '2005-06-17', '2025-09-12', 301, 2),
('김부각', '2001-07-22', '2025-09-07', 302, 1);

-- 운송 정보 테이블 (간호사, 약사, 환자 테이블의 ID 참조)
INSERT INTO transports (requested_time, sent_time, arrived_time, status, robot_id, nurse_id, pharmacist_id, patient_id) VALUES
('2025-09-06 13:00:00', '2025-09-06 13:10:00', '2025-09-06 13:25:00', 'completed', 1, 3, 1, 1),
('2025-09-07 14:50:00', '2025-09-07 15:00:00', NULL, 'in_transit', 2, 1, 2, 3),
('2025-09-07 15:05:00', NULL, NULL, 'requested', 3, 2, 1, 3),
('2025-09-07 16:05:00', NULL, NULL, 'requested', 1, 3, 1, 1);

-- 처방전 테이블 (환자 테이블의 ID 참조)
INSERT INTO prescriptions (prescription_date, period, counselling_note, patient_id) VALUES
('2025-09-06', 7, '식후 30분 복용 - 1회 아스피린 500 mg, 타이레놀 250mg', 1),
('2025-09-12', 14, '매시 복용 - 1회 항생제A 1정', 2),
('2025-09-19', 3, '통증 시 복용 - 1회 아스피린 500mg, 항생제A 1정', 3),
('2025-09-19', 14, '1일 3회 아침, 점심, 저녁 복약', 1);

-- 약 테이블
INSERT INTO drugs (name, form, dosage, unit) VALUES
('씨엠지쎄파클러캡슐', '캡슐', 1, '캡슐'),
('러키펜정', '정제', 1, '정'),
('에드로캡슐', '캡슐', 300, 'mg'),
('타리원정', '정제', 10, 'mg'),
('휴티렌정', '정제', 1, '정'),
('시네츄라시럽', '시럽', 1, '봉'),
('아스피린', '정제', 500, 'mg'),
('타이레놀', '정제', 250, 'mg'),
('항생제A', '캡슐', 1, '캡슐');

INSERT INTO transport_prescriptions (transport_id, prescription_id) VALUES
(1, 1),
(2, 3),
(3, 2),
(4, 4);

-- 처방전-약 테이블 (처방전과 약 테이블의 ID 참조)
INSERT INTO prescription_drugs (prescription_id, drug_id) VALUES
(1, 7), -- 처방전 1에 아스피린 추가
(1, 8), -- 처방전 1에 타이레놀 추가
(2, 9), -- 처방전 2에 항생제A 추가
(3, 7), -- 처방전 3에 아스피린 추가
(3, 9), -- 처방전 3에 항생제A 추가
(4, 1),
(4, 2),
(4, 3),
(4, 5);

-- 운송 정보_약 인식 상태 테이블
INSERT INTO transport_drug_recognitions (transport_id, drug_id, is_recognized) VALUES
(1, 7, TRUE),
(1, 8, TRUE),
(2, 7, TRUE),
(2, 9, TRUE),
(3, 7, FALSE),
(3, 9, FALSE),
(4, 1, FALSE),
(4, 2, FALSE),
(4, 3, FALSE),
(4, 5, FALSE);