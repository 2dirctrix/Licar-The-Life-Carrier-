-- ENUM 타입 정의
CREATE TYPE transport_status AS ENUM ('requested', 'in_transit', 'completed');

-- 간호사 테이블
CREATE TABLE nurses (
    nurse_id    SERIAL      PRIMARY KEY,
    name  VARCHAR(50) NOT NULL,
    department  VARCHAR(50)
);

-- 약사 테이블
CREATE TABLE pharmacists (
    pharmacist_id   SERIAL      PRIMARY KEY,
    name            VARCHAR(50) NOT NULL
);

-- 환자 테이블
CREATE TABLE patients (
    patient_id      SERIAL      PRIMARY KEY,
    name    VARCHAR(50) NOT NULL,
    birthday        DATE,
    admission_date  DATE,
    discharge_date  DATE,
    room_number     INTEGER,
    nurse_id        INTEGER     NOT NULL,
    FOREIGN KEY (nurse_id) REFERENCES nurses (nurse_id)
);

-- 운송 정보 테이블
CREATE TABLE transports (
    transport_id    SERIAL      PRIMARY KEY,
    requested_time  TIMESTAMP,
    sent_time     TIMESTAMP,
    arrived_time    TIMESTAMP,
    status transport_status DEFAULT 'requested',
    robot_id        INTEGER     NOT NULL,
    nurse_id        INTEGER     NOT NULL,
    pharmacist_id   INTEGER     NOT NULL,
    patient_id      INTEGER     NOT NULL,
    CHECK (
        (sent_time IS NULL OR requested_time <= sent_time) 
        AND 
        (arrived_time IS NULL OR sent_time <= arrived_time)
    ),
    FOREIGN KEY (nurse_id) REFERENCES nurses (nurse_id),
    FOREIGN KEY (pharmacist_id) REFERENCES pharmacists (pharmacist_id),
    FOREIGN KEY (patient_id) REFERENCES patients (patient_id)
);

-- 처방전 테이블
CREATE TABLE prescriptions (
    prescription_id SERIAL      PRIMARY KEY,
    prescription_date   DATE,
    period          INTEGER,
    counselling_note VARCHAR(300),
    patient_id      INTEGER     NOT NULL,
    CHECK (period > 0),
    FOREIGN KEY (patient_id) REFERENCES patients (patient_id)
);

-- 운송 정보_처방전 테이블
CREATE TABLE transport_prescriptions (
    transport_id    INTEGER NOT NULL,
    prescription_id INTEGER NOT NULL,
    PRIMARY KEY (transport_id, prescription_id),
    FOREIGN KEY (transport_id) REFERENCES transports (transport_id),
    FOREIGN KEY (prescription_id) REFERENCES prescriptions (prescription_id)
);

-- 약 정보 테이블
CREATE TABLE drugs (
    drug_id SERIAL      PRIMARY KEY,
    name    VARCHAR(50) NOT NULL,
    form    VARCHAR(20),
    dosage  INTEGER,
    unit    VARCHAR(20)
);

-- 처방전_약 정보 테이블
CREATE TABLE prescription_drugs (
    prescription_id INTEGER NOT NULL,
    drug_id         INTEGER NOT NULL,
    PRIMARY KEY (prescription_id, drug_id),
    FOREIGN KEY (prescription_id) REFERENCES prescriptions (prescription_id),
    FOREIGN KEY (drug_id) REFERENCES drugs (drug_id)
);

-- 운송 정보_약 인식 상태 테이블
CREATE TABLE transport_drug_recognitions (
    transport_id    INTEGER NOT NULL,
    drug_id         INTEGER NOT NULL,
    is_recognized   BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (transport_id, drug_id),
    FOREIGN KEY (transport_id) REFERENCES transports (transport_id),
    FOREIGN KEY (drug_id) REFERENCES drugs (drug_id)
);