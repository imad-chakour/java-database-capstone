# Smart Clinic Management System - Database Design

## MySQL Database Design

### Core Tables for Clinic Operations

#### Table: patients
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- email: VARCHAR(100), Unique, Not Null
- phone: VARCHAR(15), Not Null
- date_of_birth: DATE, Not Null
- address: TEXT
- emergency_contact: VARCHAR(100)
- medical_history_summary: TEXT
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP
- updated_at: TIMESTAMP, Default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

#### Table: doctors
- id: INT, Primary Key, Auto Increment
- first_name: VARCHAR(50), Not Null
- last_name: VARCHAR(50), Not Null
- email: VARCHAR(100), Unique, Not Null
- phone: VARCHAR(15), Not Null
- specialty: VARCHAR(50), Not Null
- license_number: VARCHAR(20), Unique, Not Null
- years_of_experience: INT
- is_active: BOOLEAN, Default True
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP
- updated_at: TIMESTAMP, Default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

#### Table: appointments
- id: INT, Primary Key, Auto Increment
- patient_id: INT, Foreign Key → patients(id), Not Null
- doctor_id: INT, Foreign Key → doctors(id), Not Null
- appointment_date: DATE, Not Null
- appointment_time: TIME, Not Null
- duration_minutes: INT, Default 30
- status: ENUM('Scheduled', 'Completed', 'Cancelled', 'No-Show'), Default 'Scheduled'
- reason_for_visit: TEXT
- notes: TEXT
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP
- updated_at: TIMESTAMP, Default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

#### Table: admin_users
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Unique, Not Null
- email: VARCHAR(100), Unique, Not Null
- password_hash: VARCHAR(255), Not Null
- role: ENUM('Super Admin', 'Clinic Manager', 'Staff'), Default 'Staff'
- is_active: BOOLEAN, Default True
- last_login: TIMESTAMP
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

#### Table: doctor_availability
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id), Not Null
- day_of_week: ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'), Not Null
- start_time: TIME, Not Null
- end_time: TIME, Not Null
- is_available: BOOLEAN, Default True
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

#### Table: clinic_locations
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), Not Null
- address: TEXT, Not Null
- phone: VARCHAR(15)
- email: VARCHAR(100)
- operating_hours: TEXT
- is_active: BOOLEAN, Default True
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

### Additional Tables for Enhanced Functionality

#### Table: prescriptions
- id: INT, Primary Key, Auto Increment
- appointment_id: INT, Foreign Key → appointments(id), Not Null
- patient_id: INT, Foreign Key → patients(id), Not Null
- doctor_id: INT, Foreign Key → doctors(id), Not Null
- medication_name: VARCHAR(100), Not Null
- dosage: VARCHAR(50), Not Null
- frequency: VARCHAR(50), Not Null
- duration_days: INT
- instructions: TEXT
- prescribed_date: DATE, Not Null
- refills_remaining: INT, Default 0
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

#### Table: payments
- id: INT, Primary Key, Auto Increment
- appointment_id: INT, Foreign Key → appointments(id), Not Null
- patient_id: INT, Foreign Key → patients(id), Not Null
- amount: DECIMAL(10,2), Not Null
- payment_method: ENUM('Cash', 'Credit Card', 'Insurance', 'Debit Card'), Not Null
- payment_status: ENUM('Pending', 'Completed', 'Failed', 'Refunded'), Default 'Pending'
- insurance_provider: VARCHAR(100)
- insurance_claim_number: VARCHAR(50)
- payment_date: TIMESTAMP
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

### Relationships and Constraints
- When a patient is deleted, their appointments should be retained for legal records (SET NULL or soft delete recommended)
- When a doctor is deleted, consider soft deletion to maintain appointment history
- Unique constraint on (doctor_id, appointment_date, appointment_time) to prevent overlapping appointments
- Indexes on frequently queried fields: patient_id, doctor_id, appointment_date, status

## MongoDB Collection Design

### Collection: patient_medical_records
```json
{
  "_id": "ObjectId('64abc123456789def123456')",
  "patientId": 101,
  "allergies": ["Penicillin", "Peanuts"],
  "chronicConditions": ["Hypertension", "Type 2 Diabetes"],
  "surgeries": [
    {
      "procedure": "Appendectomy",
      "date": "2019-03-15",
      "hospital": "General Hospital"
    }
  ],
  "familyHistory": {
    "father": ["Heart Disease", "Hypertension"],
    "mother": ["Breast Cancer"],
    "siblings": ["Asthma"]
  },
  "vitalSignsHistory": [
    {
      "date": "2024-01-15",
      "bloodPressure": "120/80",
      "heartRate": 72,
      "temperature": 98.6,
      "weight": 170,
      "height": 68,
      "recordedBy": "Dr. Smith"
    }
  ],
  "labResults": [
    {
      "testName": "Complete Blood Count",
      "testDate": "2024-01-10",
      "results": {
        "hemoglobin": "14.2 g/dL",
        "whiteBloodCells": "7.1 thou/uL",
        "platelets": "250 thou/uL"
      },
      "labName": "LabCorp",
      "attachments": ["lab_report_101_2024.pdf"]
    }
  ],
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
